package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.common.cover.WirelessEnergyReceiveCover;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessEnergyHatchPartMachine;
import cn.qiuye.gtmoremachine.integration.jade.GTJadePlugin;
import cn.qiuye.gtmoremachine.utils.BigNumberUtils;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.math.BigDecimal;
import java.util.UUID;

@GTMMDataGeneratorScanned
public class WirelessEnergyProvider extends CapabilityBlockProvider<IWirelessEnergyContainerHolder> {

    private static final String JADE_PREFIX = "config.jade.plugin_gtmoremachine";
    @GTMMRegisterLanguage(valuePrefix = GTJadePlugin.JADE_PREFIX, en = "[GTMoreMachine] Wireless Energy Monitor", cn = "[GTMoreMachine] 无线能源监视器")
    public static final String WIRELESS_ENERGY_PROVIDER = "wireless_energy_provider";
    @GTMMRegisterLanguage(en = "Total Energy: %s EU (%s A %s§r)", cn = "能源总量: %s EU (%s A %s§r)")
    public static final String WIRELESS_ENERGY_HATCH_PROVIDER_TOOLTIP_1 = JADE_PREFIX + ".wireless_energy_hatch_provider.tooltip.1";

    public WirelessEnergyProvider() {
        super(GTmm.id(WIRELESS_ENERGY_PROVIDER));
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
            data.putBoolean("IEnergyBindable", true);
            data.putUUID("uuid", capability.getUUID());
            data.putBoolean("cover", capability.cover());
            data.putString("energy", BigNumberUtils.getStringValue(capability.getWirelessEnergyContainer().getStorage()));
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("IEnergyBindable")) return;
        boolean cover = capData.getBoolean("cover");
        if (!capData.hasUUID("uuid")) {
            if (cover) {
                tooltip.add(Component.translatable(WirelessEnergyReceiveCover.WIRELESS_ENERGY_COVER_TOOLTIP_1));
            } else {
                tooltip.add(Component.translatable(WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_1));
            }
        } else {
            BigDecimal energy = BigNumberUtils.getBigDecimalValue(capData.getString("energy"));
            UUID uuid = capData.getUUID("uuid");
            var formattedEnergy = Component.literal(NumberUtils.formatBigDecimalNumberOrSic(energy)).withStyle(ChatFormatting.GOLD);
            var energyBody = Component.translatable(WIRELESS_ENERGY_HATCH_PROVIDER_TOOLTIP_1,
                    formattedEnergy,
                    Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(energy))),
                    FormattingUtil.voltageName(energy));
            if (TeamUtils.hasOwner(block.getLevel(), uuid)) {
                if (cover) {
                    tooltip.add(Component.translatable(WirelessEnergyReceiveCover.WIRELESS_ENERGY_COVER_TOOLTIP_2, TeamUtils.getName(block.getLevel(), uuid)));
                } else {
                    tooltip.add(Component.translatable(WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_2, TeamUtils.getName(block.getLevel(), uuid)));
                }
                tooltip.add(energyBody);
            } else {
                if (cover) {
                    tooltip.add(Component.translatable(WirelessEnergyReceiveCover.WIRELESS_ENERGY_COVER_TOOLTIP_3, uuid));
                } else {
                    tooltip.add(Component.translatable(WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_3, uuid));
                }
                tooltip.add(energyBody);
            }
        }
    }
}
