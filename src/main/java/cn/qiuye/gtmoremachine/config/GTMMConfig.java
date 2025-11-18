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
    @Configurable.Comment({ " " })
    public boolean isWirelessRateEnable = true;
}
