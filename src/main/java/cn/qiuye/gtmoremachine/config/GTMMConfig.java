package cn.qiuye.gtmoremachine.config;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.DataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.RegisterLanguage;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.format.ConfigFormats;

@DataGeneratorScanned
@Config(id = GTmm.MOD_ID)
public class GTMMConfig {

    @RegisterLanguage(en = "GTMM Config", cn = "GTMM 配置")
    private static final String SCREEN = "config.screen.gtmoremachine";
    public static GTMMConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(GTMMConfig.class, ConfigFormats.YAML).getConfigInstance();
            }
        }
    }

    private static final String CFGPreFix = "config.gtmoremachine.option";

    @Configurable
    @Configurable.Comment({
            "如果启用，则需要使用无线能源绑定工具绑定电池箱或者变电站来提高无线能量传输上限。",
            "If enabled, you need to use a wireless energy binding tool to bind the battery box or substation to increase the wireless energy transfer limit."
    })
    @RegisterLanguage(namePrefix = CFGPreFix, en = "Enable wireless power transfer limit.", cn = "是否启用无线能源传输限制。")
    public boolean isWirelessRateEnable = true;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    @Configurable.Comment({
            "如果启用，则每个维度需要放置维度电网传输装置来绑定维度",
            "If enabled, a Dimensional Grid Transmission Device must be placed in each dimension to bind it."
    })
    @RegisterLanguage(namePrefix = CFGPreFix, en = "Enable wireless energy dimension limit.", cn = "是否启用无线能源维度限制。")
    public boolean isWirelessDimensionRateEnable = false;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    @Configurable.Comment({
            "如果启用，则需要放置电网存储系统，可以放置多台提高总容量",
            "If enabled, Grid Storage Systems must be placed; multiple units can be installed to increase total capacity."
    })
    @RegisterLanguage(namePrefix = CFGPreFix, en = "Enable wireless power capacity limit.", cn = "是否启用无线能源容量限制。")
    public boolean isWirelessCapacitylimitEnable = false;

    @Configurable
    @Configurable.Comment({ "HUD设置", "HUD Settings" })
    @RegisterLanguage(namePrefix = CFGPreFix, en = "HUD Configuration", cn = "HUD配置")
    public HUDConfig hud = new HUDConfig();

    @DataGeneratorScanned
    public static class HUDConfig {

        @Configurable
        @Configurable.Comment({
                "设置HUD显示位置",
                "Sets HUD location",
                "左上角 (left-upper corner)",
                "右上角 (right-upper corner)",
                "左下角 (left-bottom corner)",
                "右下角 (right-bottom corner)",
                "中上   (middle-upper corner)",
                "中下   (middle-bottom corner)",
                "默认值: left-upper",
                "Default: left-upper"
        })
        @RegisterLanguage(namePrefix = CFGPreFix, en = "HUD Location", cn = "HUD位置")
        public HUDLocation hudLocation = HUDLocation.LeftUpper;

        @Configurable
        @Configurable.Comment({
                "HUD水平偏移量",
                "Horizontal offset of HUD.",
                "默认值: 0",
                "Default: 0"
        })
        @RegisterLanguage(namePrefix = CFGPreFix, en = "HUD Offset X", cn = "HUD偏移X")
        @Configurable.Range(min = -100, max = 100)
        public int hudOffsetX = 0;

        @Configurable
        @Configurable.Comment({
                "HUD垂直偏移量",
                "Vertical offset of HUD.",
                "默认值: 0",
                "Default: 0"
        })
        @RegisterLanguage(namePrefix = CFGPreFix, en = "HUD Offset Y", cn = "HUD偏移Y")
        @Configurable.Range(min = -100, max = 100)
        public int hudOffsetY = 0;
    }
}
