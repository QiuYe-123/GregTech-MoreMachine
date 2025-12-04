package cn.qiuye.gtmoremachine.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class JadeLang {

    public static void init(RegistrateLangProvider provider) {
        provider.add("config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider", "[GTMoreMachine] Wireless Energy Monitor");
        provider.add("config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1", "Total Energy: %s EU (%s A %s§r)");
    }
}
