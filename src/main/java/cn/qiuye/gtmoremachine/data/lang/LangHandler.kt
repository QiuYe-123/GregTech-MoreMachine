package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.api.lang.CNEN
import cn.qiuye.gtmoremachine.api.lang.SimplifiedChineseLanguageProvider
import cn.qiuye.gtmoremachine.api.lang.TraditionalChineseLanguageProvider
import cn.qiuye.gtmoremachine.utils.datagen.ChineseConverter

import net.minecraftforge.common.data.LanguageProvider

import com.tterrag.registrate.providers.RegistrateLangProvider
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object LangHandler {

    val LANGS = Object2ObjectOpenHashMap<String, CNEN>()

    private fun addCNEN(key: String, cnen: CNEN) {
        require(LANGS.containsKey(key)) { "Duplicate key: $key" }
        LANGS[key] = cnen
    }

    fun addCNEN(key: String, cn: String?, en: String?) {
        addCNEN(key, CNEN(cn, en))
    }

    fun addCN(key: String, cn: String) {
        addCNEN(key, cn, null)
    }

    fun addEN(key: String, en: String) {
        addCNEN(key, null, en)
    }

    fun enInitialize(provider: LanguageProvider) {
        LANGS.forEach { (k: String, v: CNEN) ->
            if (v.en() == null) return@forEach
            provider.add(k, v.en())
        }
    }
    fun cnInitialize(provider: SimplifiedChineseLanguageProvider) {
        LANGS.forEach { (k: String, v: CNEN) ->
            if (v.cn() == null) return@forEach
            provider.add(k, v.cn())
        }
    }

    fun twInitialize(provider: TraditionalChineseLanguageProvider) {
        LANGS.forEach { (k: String, v: CNEN) ->
            if (v.cn() == null) return@forEach
            provider.add(k, ChineseConverter.convert(v.cn()))
        }
    }

    fun init(provider: RegistrateLangProvider) {
        ConfigurationLang.init()
        ItemLang.init(provider)
        BlockLang.init()
        HUDLang.init(provider)
        ErrorLang.init(provider)
        KeybindingLang.init()
        MachineLang.init(provider)
        CreativeLang.init(provider)
        AdvancedTerminalLang.init(provider)
        JadeLang.init(provider)
        CuriosLang.init()
    }
}
