package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessNotifiableCWUContainer extends NotifiableComputationContainer implements IOpticalComputationProvider, IWirelessCWUContainerHolder {

    private WirelessCWUContainer container;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO handlerIO, boolean transmitter) {
        super(machine, handlerIO, transmitter);
    }

    @Override
    public boolean canBridge(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return true;
    }

    @Override
    public List<Integer> handleRecipeInner(IO io, GTRecipe recipe, List<Integer> left,
                                           boolean simulate) {
        var Container = getWirelessCWUContainer();
        if (Container == null) return left;

        int sum = left.stream().reduce(0, Integer::sum);
        if (io == IO.IN) {
            int availableCWUt = Container.getfreeCWU();
            if (availableCWUt >= sum) {
                if (recipe.data.getBoolean("duration_is_total_cwu")) {
                    int drawn = Container.download(Container.getfreeCWU(), this.machine);
                    if (!simulate) {
                        // 调整配方进度逻辑
                        if (machine instanceof IRecipeLogicMachine rlm) {
                            rlm.getRecipeLogic().setProgress(rlm.getRecipeLogic().getProgress() - 1 + drawn);
                        } else if (machine instanceof IMultiPart multiPart) {
                            for (IMultiController controller : multiPart.getControllers()) {
                                if (controller instanceof IRecipeLogicMachine rlm) {
                                    rlm.getRecipeLogic().setProgress(rlm.getRecipeLogic().getProgress() - 1 + drawn);
                                }
                            }
                        }
                    }
                    sum -= drawn;
                } else {
                    sum -= Container.download(Container.getfreeCWU(), this.machine);
                }
            }
        } else if (io == IO.OUT) {
            int canInput = this.getMaxCWUt();
            if (!simulate) {
                Container.upload(canInput, this.machine);
            }
            sum = sum - canInput;
        }
        return sum <= 0 ? null : Collections.singletonList(sum);
    }

    @Override
    public IO getHandlerIO() {
        return this.transmitter ? IO.NONE : IO.IN;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getMachine().getOwnerUUID();
    }

    @Override
    public void setWirelessCWUContainerCache(WirelessCWUContainer container) {
        this.container = container;
    }

    @Override
    public WirelessCWUContainer getWirelessCWUContainerCache() {
        return this.container;
    }
}
