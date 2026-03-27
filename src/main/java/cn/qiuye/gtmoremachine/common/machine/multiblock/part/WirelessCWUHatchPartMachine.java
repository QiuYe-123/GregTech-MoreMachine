package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.machine.trait.WirelessNotifiableCWUContainer;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WirelessCWUHatchPartMachine extends MultiblockPartMachine implements IWirelessCWUContainerHolder {

    @Getter
    @Setter
    @Nullable
    private WirelessCWUContainer wirelessCWUContainerCache;

    @Getter
    private final boolean transmitter;

    protected final WirelessNotifiableCWUContainer computationContainer;

    public WirelessCWUHatchPartMachine(BlockEntityCreationInfo holder, boolean transmitter) {
        super(holder);
        this.transmitter = transmitter;
        this.computationContainer = new WirelessNotifiableCWUContainer(this, IO.IN, transmitter);
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public InteractionResult onUseWithItem(ExtendedUseOnContext context) {
        if (isRemote()) return InteractionResult.PASS;
        ItemStack item = context.getItemInHand();
        if (item.isEmpty()) return InteractionResult.PASS;
        if (item.is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(context.getPlayer().getUUID());
            setWirelessCWUContainerCache(null);
            this.computationContainer.setWirelessCWUContainerCache(getWirelessCWUContainer());
            if (isRemote()) {
                context.getPlayer().sendSystemMessage(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.bind", TeamUtils.getName(context.getPlayer())));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, InteractionHand hand, @Nullable Direction face) {
        if (player.getItemInHand(hand).is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(null);
            setWirelessCWUContainerCache(null);
            this.computationContainer.setWirelessCWUContainerCache(getWirelessCWUContainer());
            if (isRemote()) {
                player.sendSystemMessage(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getOwnerUUID();
    }
}
