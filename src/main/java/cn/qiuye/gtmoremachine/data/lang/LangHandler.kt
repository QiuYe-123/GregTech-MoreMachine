package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object LangHandler {

    fun init(provider: RegistrateLangProvider) {
        ConfigurationLang.init(provider)
        ItemLang.init(provider)
        BlockLang.init(provider)
        HUDLang.init(provider)
        ErrorLang.init(provider)
        KeybindingLang.init(provider)
        MachineLang.init(provider)
        CreativeLang.init(provider)
        AdvancedTerminalLang.init(provider)
        JadeLang.init(provider)
        CuriosLang.init(provider)
    }
}
