package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCN
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object CreativeLang {

    fun init() {
        addCN("block.gtmoremachine.creative_energy_hatch", "§r创造模式能源仓")
        addCN("block.gtmoremachine.creative_fluid_input_hatch", "创造模式输入仓")
        addCN("block.gtmoremachine.creative_item_input_bus", "创造模式输入总线")
        addCN("block.gtmoremachine.creative_laser_hatch", "§r创造模式激光靶仓")
        addCN("item.gtmoremachine.creative_energy_cover", "创造能源覆盖板")
        addCN("item.gtmoremachine.creative_fluid_cell", "创造流体单元")
        addCNEN("item.gtmoremachine.creative_fluid_cell.tooltip.1", "§2内部流体：§f%1\$s", "§2Fluid Stored: §f%s")
        addCNEN(
            "item.gtmoremachine.creative_fluid_cell.tooltip.2",
            "右键打开设置窗口来指定流体。",
            "Right click to open GUI to set fluid.",
        )
        addCNEN(
            "item.gtmoremachine.creative_fluid_cell.tooltip.3",
            "已启用精确输出(%1\$d mB)",
            "Enabled Accurate output(%1\$d mB)",
        )
        addCNEN("item.gtmoremachine.creative_fluid_cell.gui.button.1", "启用精确输出", "Enable Accurate output")
        addCNEN("item.gtmoremachine.creative_fluid_cell.gui.button.2", "禁用精确输出", "Disable Accurate output")
        addCNEN("gtmoremachine.creative_tooltip", "§7你需要§b创造模式§7来使用它", "§7You just need Creative Mode§7 to use this")
    }
}
