package cn.qiuye.gtmoremachine.api.gui.monitor

enum class Statistics {
	Team,
	Global,
	;

	companion object : DefaultValueProvider<Statistics> {
		override fun getDefaultValue(): Statistics = Team
	}
}
