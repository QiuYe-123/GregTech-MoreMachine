package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.capability.cwu.ICWUBindable;
import cn.qiuye.gtmoremachine.common.block.machine.trait.WirelessNotifiableCWUContainer;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.data.GTItems;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WirelessCWUHatchPartMachine extends MultiblockPartMachine implements IInteractedMachine, ICWUBindable {

    @Getter
    private final WirelessNotifiableCWUContainer trait;

    public WirelessCWUHatchPartMachine(IMachineBlockEntity holder, boolean transmitter) {
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
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(player.getUUID());
            if (isRemote()) {
                player.sendSystemMessage(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.bind", TeamUtils.getName(player)));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
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
