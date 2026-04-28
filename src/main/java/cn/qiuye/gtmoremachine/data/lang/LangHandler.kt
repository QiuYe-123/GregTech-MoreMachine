package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.api.lang.CNEN
import cn.qiuye.gtmoremachine.api.lang.ChineseLangProvider
import cn.qiuye.gtmoremachine.api.registries.ScanningClass

import net.neoforged.neoforge.common.data.LanguageProvider

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object LangHandler {

	private val langs = Object2ObjectOpenHashMap<String, CNEN>()

	private fun addKeyCNEN(key: String, cnen: CNEN) {
		if (!langs.containsKey(key)) {
			langs[key] = cnen
		}
	}

	fun addCNEN(key: String, cn: String, en: String) {
		addKeyCNEN(key, CNEN(cn, en))
	}

	fun addCN(key: String, cn: String) {
		addCNEN(key, cn, "")
	}

	fun enInitialize(provider: LanguageProvider) {
		init()
		langs.forEach { (k: String, v: CNEN) ->
			if (v.en().isEmpty()) return@forEach
			provider.add(k, v.en())
		}
	}

	fun cnInitialize(provider: ChineseLangProvider) {
		langs.forEach { (k: String, v: CNEN) ->
			if (v.cn().isEmpty()) return@forEach
			provider.add(k, v.cn())
		}
	}

	fun init() {
		ScanningClass.LANG.forEach(LangHandler::addKeyCNEN)
		CreativeModeTabsLang.init()
		CreativeLang.init()
		WirelessItmeLang.init()
		WirelessMachineLang.init()
		MachineLang.init()
		BlockLang.init()
		ItemLang.init()
	}
}
