package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.addon.AddonFinder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils.big_integer_max_kong

import net.minecraft.world.level.block.Block

import org.jetbrains.annotations.ApiStatus

import java.math.BigInteger
import java.util.function.Supplier

object GTMMAPI {

    lateinit var instance: GTmm

    @JvmField
    val WECC = HashMap<ICapacityComponentData, Supplier<CapacityComponentBlock>>()

    @JvmField
    val ECU = HashMap<Int, Supplier<Block>>()

    @ApiStatus.Internal
    @JvmStatic
    fun capacityComponentBlock(tier: Int, isapacity: Boolean): BigInteger {
        AddonFinder.addons
        var number = AddonFinder.modTierMap.maxByOrNull { it.key }?.value?.getCapacityComponentBlock(tier, isapacity)
        if (number == null) {
            number = if (isapacity) {
                big_integer_max_kong.multiply(BigInteger.valueOf(tier.toLong() * tier))
            } else {
                BigInteger.ZERO
            }
        }
        return number
    }
}
