package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.common.block.machine.trait.WirelessNotifiableCWUContainer;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WirelessCWUHatchPartMachine extends MultiblockPartMachine implements IDataStickInteractable, IWirelessCWUContainerHolder {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WirelessCWUHatchPartMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Nullable
    @Getter
    @Setter
    private WirelessCWUContainer WirelessCWUContainerCache;

    protected WirelessNotifiableCWUContainer computationContainer;

    public WirelessCWUHatchPartMachine(IMachineBlockEntity holder, boolean transmitter) {
        super(holder);
        this.computationContainer = createComputationContainer(transmitter);
    }

    protected WirelessNotifiableCWUContainer createComputationContainer(Object... args) {
        IO io = IO.IN;
        if (args.length > 1 && args[args.length - 2] instanceof IO newIo) {
            io = newIo;
        }
        if (args.length > 0 && args[args.length - 1] instanceof Boolean transmitter) {
            return new WirelessNotifiableCWUContainer(this, io, transmitter, WirelessCWUContainerCache, getUUID());
        }
        throw new IllegalArgumentException();
    }

    @Override
    public @Nullable UUID getUUID() {
        return getOwnerUUID();
    }

    @Override
    public boolean canShared() {
        return false;
    }
}
