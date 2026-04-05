package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.capability.IGTMMJadeIF;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class WirelessOpticalComputationHatchProvider extends CapabilityBlockProvider<IGTMMJadeIF> {

    public WirelessOpticalComputationHatchProvider() {
        super(GTmm.id("wireless_optical_computation_hatch_provider"));
    }

    @Override
    protected @Nullable IGTMMJadeIF getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        if (MetaMachine.getMachine(level, pos) instanceof IGTMMJadeIF jadeIF) {
            return jadeIF;
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IGTMMJadeIF capability) {
        if (capability == null) return;
        data.putBoolean("isGTMMJadeIF", true);
        data.putBoolean("isTransmitter", capability.isTransmitter());
        data.putBoolean("isBinded", capability.isbinded());
        data.putString("pos", capability.getBindPos());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("isGTMMJadeIF")) return;
        if (capData.getBoolean("isBinded")) {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(Component.translatable("gtmoremachine.machine.transmitter_hatch.bind", capData.getString("pos")));
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.receiver_hatch.bind", capData.getString("pos")));
            }
        } else {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(Component.translatable("gtmoremachine.machine.transmitter_hatch.unbind"));
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.receiver_hatch.unbind"));
            }
        }
    }
}
