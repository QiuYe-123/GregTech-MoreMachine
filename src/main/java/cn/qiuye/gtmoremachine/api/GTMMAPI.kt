package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock

import net.minecraft.world.level.block.Block

import java.util.function.Supplier

object GTMMAPI {
    @JvmField
    val WECC = HashMap<ICapacityComponentData, Supplier<CapacityComponentBlock>>()

    @JvmField
    val ECU = HashMap<Int, Supplier<Block>>()
}
