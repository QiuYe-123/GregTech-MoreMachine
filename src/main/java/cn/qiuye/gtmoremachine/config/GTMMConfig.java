package cn.qiuye.gtmoremachine.config;

import cn.qiuye.gtmoremachine.GTmm;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GTmm.MOD_ID)
public class GTMMConfig {

    public static GTMMConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(GTMMConfig.class, ConfigFormats.YAML).getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Comment({ "如果启用，则需要使用无线能源绑定工具绑定电池箱或者变电站来提高无线能量传输上限。", "If enabled, you need to use a wireless energy binding tool to bind the battery box or substation to increase the wireless energy transfer limit." })
    public boolean isWirelessRateEnable = true;
    @Configurable
    @Configurable.Comment({ "如果启用，则需要使用无线能源绑定工具绑定电池箱或者变电站来提高无线能量传输上限。", "If enabled, you need to use a wireless energy binding tool to bind the battery box or substation to increase the wireless energy transfer limit." })
    public boolean isPlanetEngineRegistrationEnable = false;
}
