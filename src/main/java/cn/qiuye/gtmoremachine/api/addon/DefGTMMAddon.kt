package cn.qiuye.gtmoremachine.api.addon

import cn.qiuye.gtmoremachine.GTmm

@Suppress("unused")
@GTMMAddon
class DefGTMMAddon : IGTMMAddon {
    override fun addonModId(): String = GTmm.MOD_ID
}
