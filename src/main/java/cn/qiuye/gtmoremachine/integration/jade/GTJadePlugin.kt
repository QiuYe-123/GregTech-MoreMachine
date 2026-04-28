package cn.qiuye.gtmoremachine.integration.jade

import cn.qiuye.gtmoremachine.integration.jade.provider.WirelessCWUProvider
import cn.qiuye.gtmoremachine.integration.jade.provider.WirelessEnergyProvider
import cn.qiuye.gtmoremachine.integration.jade.provider.WirelessOpticalComputationHatchProvider

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@Suppress("unused")
@WailaPlugin
class GTJadePlugin : IWailaPlugin {

	override fun register(registration: IWailaCommonRegistration) {
		registration.registerBlockDataProvider(WirelessEnergyProvider(), BlockEntity::class.java)
		registration.registerBlockDataProvider(WirelessCWUProvider(), BlockEntity::class.java)
		registration.registerBlockDataProvider(WirelessOpticalComputationHatchProvider(), BlockEntity::class.java)
	}

	override fun registerClient(registration: IWailaClientRegistration) {
		registration.registerBlockComponent(WirelessEnergyProvider(), Block::class.java)
		registration.registerBlockComponent(WirelessCWUProvider(), Block::class.java)
		registration.registerBlockComponent(WirelessOpticalComputationHatchProvider(), Block::class.java)
	}

	companion object {
		const val JADE_PREFIX: String = "config.jade.plugin_gtmoremachine"
	}
}
