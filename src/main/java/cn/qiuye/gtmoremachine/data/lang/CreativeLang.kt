package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object CreativeLang {

    fun init(provider: RegistrateLangProvider) {
        provider.add("item.gtmoremachine.creative_fluid_cell.tooltip1", "§2Fluid Stored: §f%s")
        provider.add("item.gtmoremachine.creative_fluid_cell.tooltip2", "Right click to open GUI to set fluid.")
        provider.add("item.gtmoremachine.creative_fluid_cell.tooltip3", "Enabled Accurate output(%1\$d mB)")
        provider.add("item.gtmoremachine.creative_fluid_cell.gui.button1", "Enable Accurate output")
        provider.add("item.gtmoremachine.creative_fluid_cell.gui.button2", "Disable Accurate output")
        provider.add("gtmoremachine.creative_tooltip", "§7You just need Creative Mode§7 to use this")
    }
}
