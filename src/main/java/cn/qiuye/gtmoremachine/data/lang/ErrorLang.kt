package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object ErrorLang {

    fun init(provider: RegistrateLangProvider) {
        initpatter(provider)
    }

    fun initpatter(provider: RegistrateLangProvider) {
        provider.add(
            "gtmoremachine.multiblock.pattern.error.ecutypes",
            "§cAll heating Energy Communication Unit must be the same§r",
        )
        provider.add(
            "gtmoremachine.multiblock.pattern.error.wecc",
            "§cAll heating Wireless Energy Capacity Component must be the same§r",
        )
    }
}
