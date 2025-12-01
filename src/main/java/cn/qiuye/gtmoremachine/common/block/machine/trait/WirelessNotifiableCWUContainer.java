package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessCWUHatchPartMachine;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Collection;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessNotifiableCWUContainer extends NotifiableComputationContainer implements IOpticalComputationProvider, IWirelessCWUContainerHolder {

    private WirelessCWUContainer container;
    private final boolean transmitter;
    private TickableSubscription holder;

    public WirelessNotifiableCWUContainer(MetaMachine machine, boolean transmitter) {
        super(machine, transmitter ? IO.OUT : IO.IN, transmitter);
        this.transmitter = transmitter;
    }

    @Override
    public void onMachineLoad() {
        super.onMachineLoad();
        if (this.transmitter) {
            this.holder = this.getMachine().subscribeServerTick(this.holder, this::tick);
        }
    }

    @Override
    public void onMachineUnLoad() {
        super.onMachineUnLoad();
        if (this.holder != null) {
            this.holder.unsubscribe();
            this.holder = null;
        }
    }

    private void tick() {
        MetaMachine machine = this.machine;
        if (machine instanceof WirelessCWUHatchPartMachine wirelessmachine && this.transmitter) {
            if (!wirelessmachine.isFormed()) {
                return;
            }
            IMultiController controller = wirelessmachine.getControllers().first();
            if (controller instanceof IOpticalComputationProvider provider) {
                WirelessCWUContainer cwuContainer = this.getWirelessCWUContainer();
                if (cwuContainer != null) {
                    cwuContainer.upload(provider.requestCWUt(provider.getMaxCWUt(), false), this.machine);
                }
            }
        } else if (this.holder != null) {
            this.holder.unsubscribe();
            this.holder = null;
        }
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer cwuContainer = this.getWirelessCWUContainer();
        if (cwuContainer != null) {
	        int finalcwu = 0;
	        if (simulate) {
                finalcwu = Math.min(cwut, this.getMaxCWUt());
	        }
            GTmm.LOGGER.info(finalcwu);
	        return finalcwu;
        } else return 0;
    }

    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer cwuContainer = this.getWirelessCWUContainer();
        return cwuContainer != null ? cwuContainer.download(cwuContainer.getfreeCWU(), this.machine) : 0;
    }

    @Override
    public boolean canBridge(Collection<IOpticalComputationProvider> seen) {
        return true;
    }

    @Override
    public IO getHandlerIO() {
        return this.transmitter ? IO.OUT : IO.IN;
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
