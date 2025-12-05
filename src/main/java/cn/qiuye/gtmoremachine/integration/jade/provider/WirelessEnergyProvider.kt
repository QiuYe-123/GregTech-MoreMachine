package cn.qiuye.gtmoremachine.integration.jade.provider

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.utils.FormattingUtil
import cn.qiuye.gtmoremachine.utils.NumberUtils
import cn.qiuye.gtmoremachine.utils.TeamUtils

import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

import snownee.jade.api.BlockAccessor
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

import java.math.BigDecimal

class WirelessEnergyProvider :
    CapabilityBlockProvider<IWirelessEnergyContainerHolder?>(
        ResourceLocation.tryBuild(
            GTmm.MOD_ID,
            FormattingUtil.toLowerCaseUnderscore("wireless_energy_provider"),
        ),
    ) {
    override fun getCapability(level: Level, pos: BlockPos, side: Direction?): IWirelessEnergyContainerHolder? {
        val metaMachine = MetaMachine.getMachine(level, pos)
        if (metaMachine != null) {
            if (metaMachine is IWirelessEnergyContainerHolder && metaMachine.display()) {
                return metaMachine
            } else {
                val covers = metaMachine.getCoverContainer().covers
                for (cover in covers) {
                    if (cover is IWirelessEnergyContainerHolder && cover.display()) {
                        return cover
                    }
                }
            }
        }
        return null
    }

    override fun write(data: CompoundTag, capability: IWirelessEnergyContainerHolder?) {
        val uuid = capability?.getUUID()
        if (uuid != null) {
            data.putBoolean("isEnergyBindable", true)
            data.putUUID("uuid", uuid)
            data.putBoolean("cover", capability.cover())
            data.putString("energy", capability.getWirelessEnergyContainer()?.storage.toString())
        }
    }

    override fun addTooltip(
        capData: CompoundTag,
        tooltip: ITooltip,
        player: Player?,
        block: BlockAccessor,
        blockEntity: BlockEntity?,
        config: IPluginConfig?,
    ) {
        if (!capData.getBoolean("isEnergyBindable")) return
        val cover = capData.getBoolean("cover")
        if (!capData.hasUUID("uuid")) {
            if (cover) {
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_cover.tooltip.1"))
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.1"))
            }
        } else {
            val energy = BigDecimal(capData.getString("energy"))
            val uuid = capData.getUUID("uuid")
            if (TeamUtils.hasOwner(block.level, uuid)) {
                if (cover) {
                    tooltip.add(
                        Component.translatable(
                            "gtmoremachine.machine.wireless_energy_cover.tooltip.2",
                            TeamUtils.getName(block.level, uuid),
                        ),
                    )
                    tooltip.add(
                        Component.translatable(
                            "config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(energy),
                            ).withStyle(ChatFormatting.GOLD),
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(
                                    FormattingUtil.voltageAmperage(
                                        energy,
                                    ),
                                ),
                            ),
                            FormattingUtil.voltageName(energy),
                        ),
                    )
                } else {
                    tooltip.add(
                        Component.translatable(
                            "gtmoremachine.machine.wireless_energy_hatch.tooltip.2",
                            TeamUtils.getName(block.level, uuid),
                        ),
                    )
                    tooltip.add(
                        Component.translatable(
                            "config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(energy),
                            ).withStyle(ChatFormatting.GOLD),
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(
                                    FormattingUtil.voltageAmperage(
                                        energy,
                                    ),
                                ),
                            ),
                            FormattingUtil.voltageName(energy),
                        ),
                    )
                }
            } else {
                if (cover) {
                    tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_cover.tooltip.3", uuid))
                    tooltip.add(
                        Component.translatable(
                            "config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(energy),
                            ).withStyle(ChatFormatting.GOLD),
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(
                                    FormattingUtil.voltageAmperage(
                                        energy,
                                    ),
                                ),
                            ),
                            FormattingUtil.voltageName(energy),
                        ),
                    )
                } else {
                    tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.3", uuid))
                    tooltip.add(
                        Component.translatable(
                            "config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(energy),
                            ).withStyle(ChatFormatting.GOLD),
                            Component.literal(
                                NumberUtils.formatBigDecimalNumberOrSic(
                                    FormattingUtil.voltageAmperage(
                                        energy,
                                    ),
                                ),
                            ),
                            FormattingUtil.voltageName(energy),
                        ),
                    )
                }
            }
        }
    }
}
