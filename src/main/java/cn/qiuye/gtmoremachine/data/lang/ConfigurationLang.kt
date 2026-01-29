package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN
import cn.qiuye.gtmoremachine.utils.datagen.LangUtils

object ConfigurationLang {

    private fun cfgkey(key: String): String = LangUtils.cfgkey(GTmm.MOD_ID, key)

    fun init() {
        addCNEN(cfgkey("isWirelessRateEnable"), "是否启用无线能源传输限制。", "Enable wireless power transfer limit.")
        addCNEN(cfgkey("isWirelessDimensionRateEnable"), "是否启用无线能源维度限制。", "Enable wireless energy dimension limit.")
        addCNEN(cfgkey("isWirelessCapacitylimitEnable"), "是否启用无线能源容量限制。", "Enable wireless power capacity limit")
        addCNEN(cfgkey("hud"), "HUD配置", "HUD Configuration")
        addCNEN(cfgkey("hudLocation"), "HUD位置", "HUD Location")
        addCNEN(cfgkey("hudOffsetX"), "HUD偏移X", "HUD Offset X")
        addCNEN(cfgkey("hudOffsetY"), "HUD偏移Y", "HUD Offset Y")
    }
}
