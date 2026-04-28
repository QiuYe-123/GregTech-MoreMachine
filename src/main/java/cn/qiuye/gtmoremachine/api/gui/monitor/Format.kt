package cn.qiuye.gtmoremachine.api.gui.monitor

enum class Format {
	Science,
	Unit,
	;

	companion object : DefaultValueProvider<Format> {
		override fun getDefaultValue(): Format = Unit
	}
}
