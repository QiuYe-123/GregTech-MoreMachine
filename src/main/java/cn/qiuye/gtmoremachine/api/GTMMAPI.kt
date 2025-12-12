package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.addon.AddonFinder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock

import net.minecraft.world.level.block.Block

import org.jetbrains.annotations.ApiStatus

import java.math.BigInteger
import java.util.function.Supplier

object GTMMAPI {

    lateinit var instance: GTmm

    @JvmField
    val ECU = HashMap<Int, Supplier<Block>>()

    @JvmField
    val WECC = HashMap<ICapacityComponentData, Supplier<CapacityComponentBlock>>()

    @ApiStatus.Internal
    @JvmStatic
    fun capacityComponentBlock(tier: Int, isapacity: Boolean): BigInteger {
        AddonFinder.addons
        return AddonFinder.modTierMap.maxByOrNull { it.key }!!.value.getCapacityComponentBlock(tier, isapacity)
    }
}
