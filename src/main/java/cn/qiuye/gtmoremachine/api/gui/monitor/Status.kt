package cn.qiuye.gtmoremachine.api.gui.monitor

enum class Status {
	All,
	In,
	Out,
	;

	companion object : DefaultValueProvider<Status> {
		override fun getDefaultValue(): Status = All
	}
}
