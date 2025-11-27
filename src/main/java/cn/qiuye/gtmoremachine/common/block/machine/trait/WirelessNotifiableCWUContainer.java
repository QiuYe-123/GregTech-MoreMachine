package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class WirelessNotifiableCWUContainer extends NotifiableComputationContainer implements IWirelessCWUContainerHolder {

    @Nullable
    @Getter
    @Setter
    private WirelessCWUContainer WirelessCWUContainerCache;
    @Setter
    private UUID uuid;

    private int currentOutputCwu = 0, lastOutputCwu = 0;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO handlerIO, boolean transmitter, @Nullable WirelessCWUContainer WirelessCWUContainerCache, UUID uuid) {
        super(machine, handlerIO, transmitter);
        this.WirelessCWUContainerCache = WirelessCWUContainerCache;
        this.uuid = uuid;
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, @NotNull Collection<IOpticalComputationProvider> seen) {
        return 0;
    }

    @Override
    public int getMaxCWUt(@NotNull Collection<IOpticalComputationProvider> seen) {
        return 0;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.uuid;
    }
}
