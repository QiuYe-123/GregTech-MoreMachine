package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.api.lang.CNEN
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCN
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

import kotlin.collections.component1
import kotlin.collections.component2

object MachineLang {

	private val wemtLANGS = Object2ObjectOpenHashMap<String, CNEN>()

	private fun addkeyCNEN(key: String, cnen: CNEN) {
		if (!wemtLANGS.containsKey(key)) {
			wemtLANGS[key] = cnen
		}
	}

	private fun addKeyCNEN(key: String, cn: String, en: String) {
		addkeyCNEN(key, CNEN(cn, en))
	}

	fun wemtinit() {
		addKeyCNEN("all", "全局", "Global")
		addKeyCNEN("team", "团队", "Team")
		addKeyCNEN("science", "科学", "Science")
		addKeyCNEN("unit", "单位", "Unit")
		addKeyCNEN("power_all", "全部", "ALL")
		addKeyCNEN("power_in", "输入", "IN")
		addKeyCNEN("power_out", "输出", "OUT")
		addKeyCNEN("ascending", "升序排序", "Ascending")
		addKeyCNEN("descendingorder", "降序排序", "Descendingorder")
		addKeyCNEN("powerinteraction", "能量交互", "Power Interaction")
		addKeyCNEN("capacitycomponent", "容量组件", "Capacity Component")
		addKeyCNEN("relaynode", "中继节点", "Relay Node")
	}

	fun programmablec() {
		addCN("block.gtmoremachine.luv_programmablec_dualhatch", "§dLuV§r巨型可编程仓")
		addCN("block.gtmoremachine.luv_programmablec_hatch", "§dLuV§r可编程仓")
		addCN("block.gtmoremachine.uhv_programmablec_dualhatch", "§4UHV§r巨型可编程仓")
		addCN("block.gtmoremachine.uhv_programmablec_hatch", "§4UHV§r可编程仓")
		addCN("block.gtmoremachine.uev_programmablec_dualhatch", "§aUEV§r巨型可编程仓")
		addCN("block.gtmoremachine.uev_programmablec_hatch", "§aUEV§r可编程仓")
		addCN("block.gtmoremachine.zpm_programmablec_dualhatch", "§cZPM§r巨型可编程仓")
		addCN("block.gtmoremachine.zpm_programmablec_hatch", "§cZPM§r可编程仓")
		addCN("block.gtmoremachine.uv_programmablec_dualhatch", "§3UV§r巨型可编程仓")
		addCN("block.gtmoremachine.uv_programmablec_hatch", "§3UV§r可编程仓")
		addCN("block.gtmoremachine.uiv_programmablec_dualhatch", "§2UIV§r巨型可编程仓")
		addCN("block.gtmoremachine.uiv_programmablec_hatch", "§2UIV§r可编程仓")
		addCN("block.gtmoremachine.uxv_programmablec_dualhatch", "§eUXV§r巨型可编程仓")
		addCN("block.gtmoremachine.uxv_programmablec_hatch", "§eUXV§r可编程仓")
		addCN("block.gtmoremachine.opv_programmablec_dualhatch", "§9§lOpV§r巨型可编程仓")
		addCN("block.gtmoremachine.opv_programmablec_hatch", "§9§lOpV§r可编程仓")
		addCN("block.gtmoremachine.max_programmablec_dualhatch", "§c§lMAX§r巨型可编程仓")
		addCN("block.gtmoremachine.max_programmablec_hatch", "§c§lMAX§r可编程仓")
	}

	fun hugeBus() {
		addCN("block.gtmoremachine.ulv_huge_dual_hatch", "§8ULV§r巨型输入总成")
		addCN("block.gtmoremachine.ulv_huge_item_export_bus", "§8ULV§r巨型输出总线")
		addCN("block.gtmoremachine.ulv_huge_item_import_bus", "§8ULV§r巨型输入总线")
		addCN("block.gtmoremachine.lv_huge_dual_hatch", "§7LV§r巨型输入总成")
		addCN("block.gtmoremachine.lv_huge_item_export_bus", "§7LV§r巨型输出总线")
		addCN("block.gtmoremachine.lv_huge_item_import_bus", "§7LV§r巨型输入总线")
		addCN("block.gtmoremachine.mv_huge_dual_hatch", "§bMV§r巨型输入总成")
		addCN("block.gtmoremachine.mv_huge_item_export_bus", "§bMV§r巨型输出总线")
		addCN("block.gtmoremachine.mv_huge_item_import_bus", "§bMV§r巨型输入总线")
		addCN("block.gtmoremachine.hv_huge_dual_hatch", "§6HV§r巨型输入总成")
		addCN("block.gtmoremachine.hv_huge_item_export_bus", "§6HV§r巨型输出总线")
		addCN("block.gtmoremachine.hv_huge_item_import_bus", "§6HV§r巨型输入总线")
		addCN("block.gtmoremachine.ev_huge_dual_hatch", "§5EV§r巨型输入总成")
		addCN("block.gtmoremachine.ev_huge_item_export_bus", "§5EV§r巨型输出总线")
		addCN("block.gtmoremachine.ev_huge_item_import_bus", "§5EV§r巨型输入总线")
		addCN("block.gtmoremachine.iv_huge_dual_hatch", "§9IV§r巨型输入总成")
		addCN("block.gtmoremachine.iv_huge_item_export_bus", "§9IV§r巨型输出总线")
		addCN("block.gtmoremachine.iv_huge_item_import_bus", "§9IV§r巨型输入总线")
		addCN("block.gtmoremachine.luv_huge_dual_hatch", "§dLuV§r巨型输入总成")
		addCN("block.gtmoremachine.luv_huge_item_export_bus", "§dLuV§r巨型输出总线")
		addCN("block.gtmoremachine.luv_huge_item_import_bus", "§dLuV§r巨型输入总线")
		addCN("block.gtmoremachine.zpm_huge_dual_hatch", "§cZPM§r巨型输入总成")
		addCN("block.gtmoremachine.zpm_huge_item_export_bus", "§cZPM§r巨型输出总线")
		addCN("block.gtmoremachine.zpm_huge_item_import_bus", "§cZPM§r巨型输入总线")
		addCN("block.gtmoremachine.uv_huge_dual_hatch", "§3UV§r巨型输入总成")
		addCN("block.gtmoremachine.uv_huge_item_export_bus", "§3UV§r巨型输出总线")
		addCN("block.gtmoremachine.uv_huge_item_import_bus", "§3UV§r巨型输入总线")
		addCN("block.gtmoremachine.uhv_huge_dual_hatch", "§4UHV§r巨型输入总成")
		addCN("block.gtmoremachine.uhv_huge_item_export_bus", "§4UHV§r巨型输出总线")
		addCN("block.gtmoremachine.uhv_huge_item_import_bus", "§4UHV§r巨型输入总线")
		addCN("block.gtmoremachine.uev_huge_dual_hatch", "§aUEV§r巨型输入总成")
		addCN("block.gtmoremachine.uev_huge_item_export_bus", "§aUEV§r巨型输出总线")
		addCN("block.gtmoremachine.uev_huge_item_import_bus", "§aUEV§r巨型输入总线")
		addCN("block.gtmoremachine.uiv_huge_dual_hatch", "§2UIV§r巨型输入总成")
		addCN("block.gtmoremachine.uiv_huge_item_export_bus", "§2UIV§r巨型输出总线")
		addCN("block.gtmoremachine.uiv_huge_item_import_bus", "§2UIV§r巨型输入总线")
		addCN("block.gtmoremachine.uxv_huge_dual_hatch", "§eUXV§r巨型输入总成")
		addCN("block.gtmoremachine.uxv_huge_item_export_bus", "§eUXV§r巨型输出总线")
		addCN("block.gtmoremachine.uxv_huge_item_import_bus", "§eUXV§r巨型输入总线")
		addCN("block.gtmoremachine.opv_huge_dual_hatch", "§9§lOpV§r巨型输入总成")
		addCN("block.gtmoremachine.opv_huge_item_export_bus", "§9§lOpV§r巨型输出总线")
		addCN("block.gtmoremachine.opv_huge_item_import_bus", "§9§lOpV§r巨型输入总线")
		addCN("block.gtmoremachine.max_huge_dual_hatch", "§c§lMAX§r巨型输入总成")
		addCN("block.gtmoremachine.max_huge_item_export_bus", "§c§lMAX§r巨型输出总线")
		addCN("block.gtmoremachine.max_huge_item_import_bus", "§c§lMAX§r巨型输入总线")
	}

	fun multiblockHatch() {
		programmablec()
		hugeBus()
		addCN("block.gtmoremachine.me_export_buffer", "ME输出总成")
	}

	fun multiblock() {
		multiblockHatch()
		addCN("block.gtmoremachine.wirelsess_energy_demodulation_hub", "无线电网解调枢纽")
		addCN("block.gtmoremachine.wirelsess_energy_dimensional_relay_node", "无线电网维度中继节点")
	}

	fun init() {
		wemtinit()
		multiblock()
		wemtLANGS.forEach { (k: String, v: CNEN) ->
			if (v.cn() == null) return@forEach
			addCNEN("gtmoremachine.machine.wireless_energy_monitor.tooltip.$k", v.cn, v.en)
		}
	}
}
