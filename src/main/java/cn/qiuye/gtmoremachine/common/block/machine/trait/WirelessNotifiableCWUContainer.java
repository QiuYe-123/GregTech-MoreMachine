package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessNotifiableCWUContainer extends NotifiableComputationContainer
                                            implements IOpticalComputationProvider, IWirelessCWUContainerHolder {

    private WirelessCWUContainer container;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO io,boolean transmitter) {
        super(machine, io, transmitter);
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer cwuContainer = this.getWirelessCWUContainer();
        if (cwuContainer != null) {
            int finalcwu = 0;
            if (simulate) {
                finalcwu = Math.min(cwut, this.getMaxCWUt());
                finalcwu = cwuContainer.download(finalcwu, this.machine);
            }
            return finalcwu;
        } else return 0;
    }

    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer cwuContainer = this.getWirelessCWUContainer();
        return cwuContainer != null ? cwuContainer.getfreeCWU() : 0;
    }

    @Override
    public boolean canBridge(Collection<IOpticalComputationProvider> seen) {
        return true;
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
