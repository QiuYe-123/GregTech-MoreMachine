package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.integration.ae.machine.multiblockmachine.GTMMAEMultiMachines

object MultiMachines {

    @JvmStatic
    fun init() {
        WirelessMultiMachines.init()
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEMultiMachines.init()
        }
    }
}
