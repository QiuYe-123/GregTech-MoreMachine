package cn.qiuye.gtmoremachine.common.data.machines

import cn.qiuye.gtmoremachine.GTmm

object Machines {

    @JvmStatic
    fun init() {
        CustomMachines.init()
        WirelessMachines.init()
        CreativeMachines.init()
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEMachines.init()
        }
    }
}
