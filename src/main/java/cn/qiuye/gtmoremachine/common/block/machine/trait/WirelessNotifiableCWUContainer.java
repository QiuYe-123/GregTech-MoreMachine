package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessCWUHatchPartMachine;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
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

    private int currentOutputCwu = 0, lastOutputCwu = 0;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO handlerIO, boolean transmitter) {
        super(machine, handlerIO, transmitter);
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        var latestTimeStamp = getMachine().getOffsetTimer();
        if (lastTimeStamp < latestTimeStamp) {
            lastOutputCwu = currentOutputCwu;
            currentOutputCwu = 0;
            lastTimeStamp = latestTimeStamp;
        }

        seen.add(this);
        if (handlerIO == IO.IN) {
            if (isTransmitter()) {
                // Ask the Multiblock controller, which *should* be an IOpticalComputationProvider
                if (machine instanceof IOpticalComputationProvider provider) {
                    return provider.requestCWUt(cwut, simulate, seen);
                } else if (machine instanceof IMultiPart part) {
                    if (part.getControllers().isEmpty()) {
                        return 0;
                    }
                    for (IMultiController controller : part.getControllers()) {
                        if (controller instanceof IOpticalComputationProvider provider) {
                            return provider.requestCWUt(cwut, simulate, seen);
                        }
                        for (MachineTrait trait : controller.self().getTraits()) {
                            if (trait instanceof IOpticalComputationProvider provider) {
                                return provider.requestCWUt(cwut, simulate, seen);
                            }
                        }
                    }
                    GTmm.LOGGER
                            .error("NotifiableComputationContainer could request CWU/t from its machine's controller!");
                    return 0;
                } else {
                    GTmm.LOGGER.error("NotifiableComputationContainer could request CWU/t from its machine!");
                    return 0;
                }
            } else {
                // Ask the attached Transmitter hatch, if it exists
                IOpticalComputationProvider provider = getOpticalNetProvider();
                if (provider == null) return 0;
                return provider.requestCWUt(cwut, simulate, seen);
            }
        } else {
            lastOutputCwu = lastOutputCwu - cwut;
            return Math.min(lastOutputCwu, cwut);
        }
    }

    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        if (handlerIO == IO.IN) {
            if (isTransmitter()) {
                // Ask the Multiblock controller, which *should* be an IOpticalComputationProvider
                if (machine instanceof IOpticalComputationProvider provider) {
                    return provider.getMaxCWUt(seen);
                } else if (machine instanceof IMultiPart part) {
                    if (part.getControllers().isEmpty()) {
                        return 0;
                    }
                    for (IMultiController controller : part.getControllers()) {
                        if (!controller.isFormed()) {
                            continue;
                        }
                        if (controller instanceof IOpticalComputationProvider provider) {
                            return provider.getMaxCWUt(seen);
                        }
                        for (MachineTrait trait : controller.self().getTraits()) {
                            if (trait instanceof IOpticalComputationProvider provider) {
                                return provider.getMaxCWUt(seen);
                            }
                        }
                    }
                    GTmm.LOGGER.error(
                            "NotifiableComputationContainer could not get maximum CWU/t from its machine's controller!");
                    return 0;
                } else {
                    GTmm.LOGGER.error("NotifiableComputationContainer could not get maximum CWU/t from its machine!");
                    return 0;
                }
            } else {
                // Ask the attached Transmitter hatch, if it exists
                IOpticalComputationProvider provider = getOpticalNetProvider();
                if (provider == null) return 0;
                return provider.getMaxCWUt(seen);
            }
        } else {
            return lastOutputCwu;
        }
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
        IOpticalComputationProvider provider = getOpticalNetProvider();
        if (provider == null) return left;

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

    @Nullable
    private IOpticalComputationProvider getOpticalNetProvider() {
        if (machine instanceof WirelessCWUHatchPartMachine woc) {
            var transmitterMachine = MetaMachine.getMachine(machine.getLevel(), woc.getPos());
            if (transmitterMachine instanceof WirelessCWUHatchPartMachine transmitter) {
                return transmitter.getTrait();
            }
        }
        return null;
    }
}
