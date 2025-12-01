package cn.qiuye.gtmoremachine.common.block.machine.trait;

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
        super(machine, IO.BOTH, transmitter);
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
        if (machine instanceof WirelessCWUHatchPartMachine wirelessmachine) {
            if (!wirelessmachine.isFormed()) {
                return;
            }
            IMultiController controller = wirelessmachine.getControllers().first();
            if (controller instanceof IOpticalComputationProvider provider) {
                WirelessCWUContainer var4 = this.getWirelessCWUContainer();
                if (var4 != null && var4.getStorage().compareTo(BigInteger.ZERO) > 0) {
                    var4.upload(provider.requestCWUt(BigIntegerUtils.getIntValue(var4.getStorage()), false), this.machine);
                }
            }
        } else if (this.holder != null) {
            this.holder.unsubscribe();
            this.holder = null;
        }
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer var1 = this.getWirelessCWUContainer();
        if (var1 != null) {
            int var2 = BigIntegerUtils.getIntValue(var1.getStorage());
            int var3 = Math.min(cwut, var2);
            if (simulate) {
                var1.download(var2, this.machine);
            }
            return var3;
        } else return 0;
    }

    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        WirelessCWUContainer var1 = this.getWirelessCWUContainer();
        return var1 != null ? BigIntegerUtils.getIntValue(var1.getStorage()) : 0;
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
        return this.machine.getOwnerUUID();
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
