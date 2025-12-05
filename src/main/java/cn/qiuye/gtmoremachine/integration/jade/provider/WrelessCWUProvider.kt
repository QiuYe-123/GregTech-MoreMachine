package cn.qiuye.gtmoremachine.integration.jade.provider

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessCWUHatchPartMachine
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils
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

class WrelessCWUProvider :
    CapabilityBlockProvider<WirelessCWUHatchPartMachine?>(
        ResourceLocation.tryBuild(
            GTmm.MOD_ID,
            FormattingUtil.toLowerCaseUnderscore("wireless_cwu_provider"),
        ),
    ) {
    override fun getCapability(level: Level, pos: BlockPos, side: Direction?): WirelessCWUHatchPartMachine? {
        val metamachine = MetaMachine.getMachine(level, pos)
        if (metamachine is WirelessCWUHatchPartMachine && metamachine.display()) {
            return metamachine
        }
        return null
    }

    override fun write(data: CompoundTag, capability: WirelessCWUHatchPartMachine?) {
        val uuid = capability?.uuid
        if (uuid != null) {
            data.putBoolean("isCWUBindable", true)
            data.putUUID("UUID", uuid)
            data.putString("cwu", BigIntegerUtils.getStringValue(capability.trait.getWirelessCWUContainer()!!.storage))
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
        if (!capData.getBoolean("isCWUBindable")) return
        if (!capData.hasUUID("UUID")) {
            tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.1"))
        } else {
            val cwu = BigDecimal(capData.getString("cwu"))
            val uuid = capData.getUUID("UUID")
            if (TeamUtils.hasOwner(block.getLevel(), uuid)) {
                tooltip.add(
                    Component.translatable(
                        "gtmoremachine.machine.wireless_energy_hatch.tooltip.2",
                        TeamUtils.getName(block.getLevel(), uuid),
                    ),
                )
                tooltip.add(
                    Component.translatable(
                        "config.jade.plugin_gtmoremachine.wireless_cwu_hatch_provider.tooltip.1",
                        Component.literal(NumberUtils.formatBigDecimalNumberOrSic(cwu)).withStyle(ChatFormatting.GOLD),
                    ),
                )
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.3", uuid))
                tooltip.add(
                    Component.translatable(
                        "config.jade.plugin_gtmoremachine.wireless_cwu_hatch_provider.tooltip.1",
                        Component.literal(NumberUtils.formatBigDecimalNumberOrSic(cwu)).withStyle(ChatFormatting.GOLD),
                    ),
                )
            }
        }
    }
}
