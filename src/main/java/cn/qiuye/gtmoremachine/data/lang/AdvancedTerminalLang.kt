package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.common.block.BlockMap
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object AdvancedTerminalLang {

    fun levelbockinit() {
        addCNEN(BlockMap.COIL, "线圈", "Coil")
        addCNEN(BlockMap.COMP, "电容", "Capacitor")
        addCNEN(BlockMap.CLEA, "过滤机械方块", "CleamRoomType")
        addCNEN(BlockMap.LAMP, "灯", "Lamp")
        addCNEN(BlockMap.BORLAMP, "无框灯", "Borderless Lamp")
        addCNEN(BlockMap.ROTOR, "转子支架", "Rotor Holder")
        addCNEN(BlockMap.WECC, "电网容量组件", "Wireless Energy Capacity Component")
        addCNEN(BlockMap.ECU, "能源通讯单元", "Energy Communication Unit")
    }

    fun init() {
        levelbockinit()
        addCNEN("item.gtmoremachine.advanced_terminal.setting.title", "高级终端设置", "§bAdvanced Terminal")
        addCNEN("item.gtmoremachine.advanced_terminal.setting.1", "等级方块", "Level Block")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.1.tooltip",
            "设置优先自动放置的等级方块(点击打开)。",
            "Set the priority level for automatic Tier Block placement (Click to open).",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.2", "重复结构次数", "Number of repetitions of the structure")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.2.tooltip",
            "用于设置可重复结构(蒸馏塔、装配线等)的重复部分放置次数",
            "Used to set the number of repetitions for the placement of repeating parts in structures like distillation towers, assembly lines, etc.",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.3", "无仓室模式", "No Hatch mode")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.3.tooltip",
            "是否启用无仓室模式(OFF:不启用,ON:启用)\\n启用无仓室模式后不会在非唯一时放置各种仓室。",
            "Whether to enable the no-Hatch mode (OFF: not enabled, ON: enabled)\nAfter enabling the no-Hatch mode, various Hatches will not be placed when they are not unique.",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.4", "替换模式", "Replace mode")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.4.tooltip",
            "是否启用等级方块替换模式(OFF:不启用,ON:启用)\n启用等级方块替换模式会将所有线圈替换为等级方块中指定的等级。",
            "Whether to enable the Tier Block replace mode (OFF: not enabled, ON: enabled)\nAfter enabling the Tier Block replace mode, all coils will be replaced by the Tier Block specified in the Tier Block setting.",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.5", "使用AE物品", "Use AE Items")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.5.tooltip",
            "是否使用AE物品(OFF:不使用,ON:使用)\n使用AE物品开启后，会通过背包中的AE终端连接到相应的AE网络并使用其中的物品来进行建造。",
            "Whether to use AE items (OFF: not use, ON: use)\nAfter enabling 'Use AE Items', it will connect to the corresponding AE network via the AE Terminal in the inventory and use the items from it for construction.",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.6", "镜像搭建", "Mirror Building")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.6.tooltip",
            "是否启用镜像搭建(OFF:不启用,ON:启用)\n启用镜像搭建后，会将所有结构的镜像进行搭建。",
            "Whether to enable Mirror Building (OFF: not enabled, ON: enabled)\nAfter enabling Mirror Building, it will build the mirror image of all structures.",
        )
        addCNEN("item.gtmoremachine.advanced_terminal.setting.7", "拆除模式", "Dismantle mode")
        addCNEN(
            "item.gtmoremachine.advanced_terminal.setting.7.tooltip",
            "是否启用拆除模式(OFF:不启用,ON:启用)\n启用拆除模式后，会将设定好重复结构次数的部分结构进行拆除。\n警告:会将结构内的所有方块拆除",
            "Whether to enable Dismantle mode (OFF: not enabled, ON: enabled)\nAfter enabling Dismantle mode, it will dismantle the structural parts for which the number of repetitions has been set.\nWarning: All blocks within the structure will be dismantled.",
        )
    }
}
