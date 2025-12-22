package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.common.block.BlockMap

import com.tterrag.registrate.providers.RegistrateLangProvider

object AdvancedTerminalLang {

    fun init(provider: RegistrateLangProvider) {
        provider.add("item.gtmoremachine.advanced_terminal.setting.title", "§bAdvanced Terminal")
        provider.add("item.gtmoremachine.advanced_terminal.setting.1", "Level Block")
        provider.add(BlockMap.COIL, "Coil")
        provider.add(BlockMap.COMP, "Capacitor")
        provider.add(BlockMap.CLEA, "CleamRoomType")
        provider.add(BlockMap.LAMP, "Lamp")
        provider.add(BlockMap.BORLAMP, "Borderless Lamp")
        provider.add(BlockMap.ROTOR, "Rotor Holder")
        provider.add(BlockMap.WECC, "Wireless Energy Capacity Component")
        provider.add(BlockMap.ECU, "Energy Communication Unit")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.1.tooltip",
            "Set the priority level for automatic Tier Block placement (Click to open).",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.2", "Number of repetitions of the structure")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.2.tooltip",
            "Used to set the number of repetitions for the placement of repeating parts in structures like distillation towers, assembly lines, etc.",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.3", "No Hatch mode")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.3.tooltip",
            "Whether to enable the no-Hatch mode (OFF: not enabled, ON: enabled)\nAfter enabling the no-Hatch mode, various Hatches will not be placed when they are not unique.",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.4", "Replace mode")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.4.tooltip",
            "Whether to enable the Tier Block replace mode (OFF: not enabled, ON: enabled)\nAfter enabling the Tier Block replace mode, all coils will be replaced by the Tier Block specified in the Tier Block setting.",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.5", "Use AE Items")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.5.tooltip",
            "Whether to use AE items (OFF: not use, ON: use)\nAfter enabling 'Use AE Items', it will connect to the corresponding AE network via the AE Terminal in the inventory and use the items from it for construction.",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.6", "Mirror Building")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.6.tooltip",
            "Whether to enable Mirror Building (OFF: not enabled, ON: enabled)\nAfter enabling Mirror Building, it will build the mirror image of all structures.",
        )
        provider.add("item.gtmoremachine.advanced_terminal.setting.7", "Dismantle mode")
        provider.add(
            "item.gtmoremachine.advanced_terminal.setting.7.tooltip",
            "Whether to enable Dismantle mode (OFF: not enabled, ON: enabled)\nAfter enabling Dismantle mode, it will dismantle the structural parts for which the number of repetitions has been set.\nWarning: All blocks within the structure will be dismantled.",
        )
    }
}
