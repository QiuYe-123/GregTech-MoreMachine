package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock

import java.util.function.Supplier

object GTMMAPI {
    @JvmField
    val WECC = HashMap<ICapacityComponentData, Supplier<CapacityComponentBlock>>()
}
