package cn.qiuye.gtmoremachine.config

import cn.qiuye.gtmoremachine.GTmm

import dev.toma.configuration.Configuration
import dev.toma.configuration.config.Config
import dev.toma.configuration.config.Configurable
import dev.toma.configuration.config.format.ConfigFormats

@Config(id = GTmm.MOD_ID)
class GTMMConfig {

    @Configurable
    @Configurable.Comment(
        "如果启用，则需要使用无线能源绑定工具绑定电池箱或者变电站来提高无线能量传输上限。",
        "If enabled, you need to use a wireless energy binding tool to bind the battery box or substation to increase the wireless energy transfer limit.",
    )
    @JvmField
    var isWirelessRateEnable: Boolean = true

    companion object {
        @JvmStatic
        val INSTANCE: GTMMConfig by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Configuration.registerConfig(GTMMConfig::class.java, ConfigFormats.YAML).configInstance
        }
        fun init() {
            INSTANCE
        }
    }
}
