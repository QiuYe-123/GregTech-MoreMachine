package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
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

import java.math.BigDecimal;
import java.util.UUID;

public class WirelessEnergyProvider extends CapabilityBlockProvider<IWirelessEnergyContainerHolder> {

    public WirelessEnergyProvider() {
        super(ResourceLocation.tryBuild(GTmm.MOD_ID, FormattingUtil.toLowerCaseUnderscore("wireless_energy_provider")));
    }

    @Override
    protected @Nullable IWirelessEnergyContainerHolder getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        var metaMachine = MetaMachine.getMachine(level, pos);
        if (metaMachine != null) {
            if (metaMachine instanceof IWirelessEnergyContainerHolder bindable && bindable.display()) {
                return bindable;
            } else {
                var covers = metaMachine.getCoverContainer().getCovers();
                for (var cover : covers) {
                    if (cover instanceof IWirelessEnergyContainerHolder bindable && bindable.display()) {
                        return bindable;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IWirelessEnergyContainerHolder capability) {
        if (capability.getUUID() != null) {
            data.putBoolean("isBindable", true);
            data.putUUID("uuid", capability.getUUID());
            data.putBoolean("cover", capability.cover());
            data.putString("energy", BigIntegerUtils.getStringValue(capability.getWirelessEnergyContainer().getStorage()));
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("isBindable")) return;

        boolean cover = capData.getBoolean("cover");
        String baseKey = cover ? "gtmoremachine.machine.wireless_energy_cover.tooltip" : "gtmoremachine.machine.wireless_energy_hatch.tooltip";

        if (!capData.hasUUID("uuid")) {
            tooltip.add(Component.translatable(baseKey + ".1"));
            return;
        }

        BigDecimal energy = BigIntegerUtils.setBigDecimalValue(capData.getString("energy"));
        UUID uuid = capData.getUUID("uuid");
        Component ownerInfo = TeamUtils.hasOwner(block.getLevel(), uuid) ? TeamUtils.getName(block.getLevel(), uuid) : Component.nullToEmpty(uuid.toString());
        String ownerKeySuffix = TeamUtils.hasOwner(block.getLevel(), uuid) ? "2" : "3";

        tooltip.add(Component.translatable(baseKey + "." + ownerKeySuffix, ownerInfo));
        tooltip.add(Component.translatable("config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(energy)).withStyle(ChatFormatting.GOLD),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(energy))),
                FormattingUtil.voltageName(energy)));
    }
}
