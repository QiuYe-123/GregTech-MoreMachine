package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object JadeLang {

    fun init(provider: RegistrateLangProvider) {
        provider.add(
            "config.jade.plugin_gtmoremachine.wireless_energy_provider",
            "[GTMoreMachine] Wireless Energy Monitor",
        )
        provider.add(
            "config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
            "Total Energy: %s EU (%s A %s§r)",
        )
        provider.add(
            "config.jade.plugin_gtmoremachine.wireless_optical_computation_hatch_provider",
            "[GTMoreMachine] Wireless OpticalComputation",
        )
        provider.add("config.jade.plugin_gtmoremachine.wireless_cwu_provider", "[GTMoreMachine] Wireless CWU Monitor")
        provider.add("config.jade.plugin_gtmoremachine.wireless_cwu_hatch_provider.tooltip.1", "Total CWU: %s CWU")
    }
}
