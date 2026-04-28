package cn.qiuye.gtmoremachine.api.gui.monitor

enum class Type {
	PowerInteraction,
	Capacitycomponent,
	RelayNode,
	;

	companion object : DefaultValueProvider<Type> {
		override fun getDefaultValue(): Type = Capacitycomponent
	}
}
