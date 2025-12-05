package cn.qiuye.gtmoremachine.integration.jade.provider

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.capability.IGTMMJadeIF
import cn.qiuye.gtmoremachine.utils.FormattingUtil

import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider

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

class WirelessOpticalComputationHatchProvider :
    CapabilityBlockProvider<IGTMMJadeIF?>(
        ResourceLocation.tryBuild(
            GTmm.MOD_ID,
            FormattingUtil.toLowerCaseUnderscore("wireless_optical_computation_hatch_provider"),
        ),
    ) {
    override fun getCapability(level: Level, pos: BlockPos, side: Direction?): IGTMMJadeIF? {
        if (MetaMachine.getMachine(level, pos) is IGTMMJadeIF) {
            val jadeIF = MetaMachine.getMachine(level, pos) as IGTMMJadeIF
            return jadeIF
        }
        return null
    }

    override fun write(data: CompoundTag, capability: IGTMMJadeIF?) {
        if (capability == null) return
        data.putBoolean("isGTMMJadeIF", true)
        data.putBoolean("isTransmitter", capability.isTransmitter())
        data.putBoolean("isBinded", capability.isbinded())
        data.putString("pos", capability.getBindPos())
    }

    override fun addTooltip(
        capData: CompoundTag,
        tooltip: ITooltip,
        player: Player?,
        block: BlockAccessor?,
        blockEntity: BlockEntity?,
        config: IPluginConfig?,
    ) {
        if (!capData.getBoolean("isGTMMJadeIF")) return
        if (capData.getBoolean("isBinded")) {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(
                    Component.translatable(
                        "gtmoremachine.machine.transmitter_hatch.bind",
                        capData.getString("pos"),
                    ),
                )
            } else {
                tooltip.add(
                    Component.translatable(
                        "gtmoremachine.machine.receiver_hatch.bind",
                        capData.getString("pos"),
                    ),
                )
            }
        } else {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(Component.translatable("gtmoremachine.machine.transmitter_hatch.unbind"))
            } else {
                tooltip.add(Component.translatable("gtmoremachine.machine.receiver_hatch.unbind"))
            }
        }
    }
}
