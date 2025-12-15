package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object KeybindingLang {
    fun init(provider: RegistrateLangProvider) {
        provider.add(
            "key.gtmoremachine.category",
            "GT More Machine",
        )
        provider.add(
            "key.gtmoremachine.bind.wet",
            "Wireless Energy Terminal",
        )
    }
}
