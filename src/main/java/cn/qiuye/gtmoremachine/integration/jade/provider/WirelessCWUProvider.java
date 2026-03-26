package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.FormattingUtil;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.math.BigInteger;
import java.util.UUID;

public class WirelessCWUProvider extends CapabilityBlockProvider<IWirelessCWUContainerHolder> {

    public WirelessCWUProvider() {
        super(ResourceLocation.tryBuild(GTmm.MOD_ID, FormattingUtil.toLowerCaseUnderscore("wireless_cwu_provider")));
    }

    @Override
    protected @Nullable IWirelessCWUContainerHolder getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        var metamachine = MetaMachine.getMachine(level, pos);
        if (metamachine instanceof IWirelessCWUContainerHolder bindable && bindable.display()) {
            return bindable;
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IWirelessCWUContainerHolder capability) {
        if (capability.getUUID() != null) {
            data.putBoolean("isCWUBindable", true);
            data.putUUID("UUID", capability.getUUID());
            data.putString("cwu", BigIntegerUtils.getStringValue(capability.getWirelessCWUContainer().getStorage()));
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("isCWUBindable")) return;
        if (!capData.hasUUID("UUID")) {
            tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.1"));
        } else {
            BigInteger cwu = BigIntegerUtils.setBigIntegerValue(capData.getString("cwu"));
            UUID uuid = capData.getUUID("UUID");
            if (TeamUtils.hasOwner(block.getLevel(), uuid)) {
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.2", TeamUtils.getName(block.getLevel(), uuid)));
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_cwu_monitor.tooltip.1",
                        Component.literal(NumberUtils.formatBigIntegerNumberOrSic(cwu)).withStyle(ChatFormatting.GOLD)));
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.3", uuid));
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_cwu_monitor.tooltip.1",
                        Component.literal(NumberUtils.formatBigIntegerNumberOrSic(cwu)).withStyle(ChatFormatting.GOLD)));
            }
        }
    }
}
