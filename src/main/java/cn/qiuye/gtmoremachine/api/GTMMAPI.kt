package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.addon.AddonFinder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICCData
import cn.qiuye.gtmoremachine.api.machine.multiblock.IECUBlock
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock
import cn.qiuye.gtmoremachine.common.block.ECUBlock

import org.jetbrains.annotations.ApiStatus

import java.math.BigInteger
import java.util.function.Supplier

object GTMMAPI {

	lateinit var instance: GTmm

	@JvmField
	val ECU = HashMap<IECUBlock, Supplier<ECUBlock>>()

	@JvmField
	val WECC = HashMap<ICCData, Supplier<CapacityComponentBlock>>()

	@ApiStatus.Internal
	@JvmStatic
	fun capacityComponentBlock(tier: Int, isapacity: Boolean): BigInteger {
		AddonFinder.addons
		return AddonFinder.modTierMap.maxByOrNull { it.key }!!.value.getCapacityComponentBlock(tier, isapacity)
	}
}
