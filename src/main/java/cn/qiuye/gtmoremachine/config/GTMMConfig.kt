package cn.qiuye.gtmoremachine.config

import cn.qiuye.gtmoremachine.GTmm

import dev.toma.configuration.Configuration
import dev.toma.configuration.config.Config
import dev.toma.configuration.config.Configurable
import dev.toma.configuration.config.UpdateRestrictions
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

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    @Configurable.Comment(
        "如果启用，则每个维度需要放置维度电网传输装置来绑定维度",
        "If enabled, a Dimensional Grid Transmission Device must be placed in each dimension to bind it.",
    )
    @JvmField
    var isWirelessDimensionRateEnable: Boolean = false

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    @Configurable.Comment(
        "如果启用，则需要放置电网存储系统，可以放置多台提高总容量",
        "If enabled, Grid Storage Systems must be placed; multiple units can be installed to increase total capacity.",
    )
    @JvmField
    var isWirelessCapacitylimitEnable: Boolean = false

    @Configurable
    @Configurable.Range(min = 1, max = 6)
    @JvmField
    var wirelessAlign: Int = 1

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
