package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object HUDLang {
    fun init(provider: RegistrateLangProvider) {
        provider.add("item.gtmoremachine.wireless_energy_terminal.hud.1", "Average Net ALL：%s EU (%s A %s§r)")
        provider.add("item.gtmoremachine.wireless_energy_terminal.hud.2", "Average Net IN：%s EU (%s A %s§r)")
        provider.add("item.gtmoremachine.wireless_energy_terminal.hud.3", "Average Net OUT：%s EU (%s A %s§r)")
        provider.add("item.gtmoremachine.wireless_energy_terminal.hud.4", "Total Energy：%s EU (%s A %s§r)")
    }
}
