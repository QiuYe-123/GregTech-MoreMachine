package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCN

object ItemLang {

	fun programmablec() {
		addCN("item.gtmoremachine.programmable_cover", "可编程覆盖板")
		addCN("item.gtmoremachine.virtual_item_provider", "虚拟物品提供器")
		addCN("item.gtmoremachine.virtual_item_provider_cell", "虚拟物品提供器元件")
	}

	fun itme() {
		addCN("item.gtmoremachine.advanced_terminal", "§b高级终端")
	}

	fun init() {
		programmablec()
		itme()
	}
}
