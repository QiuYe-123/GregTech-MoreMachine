package cn.qiuye.gtmoremachine.api.gui.monitor

enum class Sorting {
    Ascending,
    Descendingorder,
    ;

    companion object : DefaultValueProvider<Sorting> {
        override fun getDefaultValue(): Sorting = Ascending
    }
}
