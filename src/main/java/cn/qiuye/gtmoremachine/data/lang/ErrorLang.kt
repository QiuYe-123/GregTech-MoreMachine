package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object ErrorLang {

	fun init() {
		initpatter()
	}

	fun initpatter() {
		addCNEN(
			"gtmoremachine.multiblock.pattern.error.ecutypes",
			"§c必须使用同种能源通讯单元r",
			"§cAll heating Energy Communication Unit must be the same§r",
		)
		addCNEN(
			"gtmoremachine.multiblock.pattern.error.wecc",
			"§c必须使用相同的电网容量组件§§r",
			"§cAll heating Wireless Energy Capacity Component must be the same§r",
		)
	}
}
