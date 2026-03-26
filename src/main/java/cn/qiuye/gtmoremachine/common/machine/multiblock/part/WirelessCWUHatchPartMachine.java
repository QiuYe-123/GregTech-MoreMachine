package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.capability.cwu.ICWUBindable;
import cn.qiuye.gtmoremachine.common.block.machine.trait.WirelessNotifiableCWUContainer;
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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WirelessCWUHatchPartMachine extends MultiblockPartMachine implements ICWUBindable {

    @Getter
    private final WirelessNotifiableCWUContainer trait;

    public WirelessCWUHatchPartMachine(BlockEntityCreationInfo holder, boolean transmitter) {
        super(holder);
        this.trait = createComputationContainer(transmitter);
    }

    protected WirelessNotifiableCWUContainer createComputationContainer(Object... args) {
        IO io = IO.IN;
        if (args.length > 1 && args[args.length - 2] instanceof IO newIo) {
            io = newIo;
        }
        if (args.length > 0 && args[args.length - 1] instanceof Boolean transmitter) {
            return new WirelessNotifiableCWUContainer(this, io, transmitter);
        }
        throw new IllegalArgumentException();
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
            if (isRemote()) {
                player.sendSystemMessage(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getTrait().getUUID();
    }
}
