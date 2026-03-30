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
		addCNEN("gtmoremachine.universal.disabled", "多方块结构共享：§4禁止", "Multiblock Sharing §4Disabled")
		addCNEN(
			"block.gtmoremachine.capacity_component.tooltip_empty",
			"§7用于填补无线电网解调枢纽的结构空隙",
			"§7For filling structural gaps in the Wireless Energy Storage Module",
		)
		addCNEN(
			"block.gtmoremachine.capacity_component.tooltip_filled",
			"§c容量组件容量：§f%d EU",
			"§cCapacity component capacity: §f%d EU",
		)
		addCNEN(
			"block.gtmoremachine.capacity_component.tooltip_passive_drain",
			"§c容量组件被动耗能：§f%d EU",
			"§cCapacity component passive energy consumption: §f%d EU",
		)
		addCNEN(
			"gtmoremachine.machine.huge_item_bus.import.tooltip",
			"为多方块结构输入物品，每个槽位最多可存储2^31-1个物品。",
			"Inputs items for multiblock structures, with each slot able to store up to 2^31-1 items.",
		)
		addCNEN(
			"gtmoremachine.machine.huge_item_bus.export.tooltip",
			"为多方块结构输出物品，每个槽位最多可存储2^31-1个物品。",
			"Outputs items for multiblock structures, with each slot able to store up to 2^31-1 items.",
		)
		addCNEN(
			"gtmoremachine.machine.huge_item_bus.tooltip.1",
			"退回所有物品到面前的容器中。",
			"Returns all items to the container in front.",
		)
		addCNEN("gtmoremachine.machine.huge_item_bus.tooltip.2", "物品槽位: %s/%s", "Item slots: %s/%s")
		addCNEN("gtmoremachine.machine.huge_item_bus.tooltip.3", "空", "Empty")
		addCNEN("gtmoremachine.machine.huge_dual_hatch.tooltip.2", "流体槽位: %s/%s", "Fluid slots: %s/%s")
		addCNEN("gui.gtmoremachine.share_inventory.title", "催化剂", "Catalyst")
		addCNEN("gui.gtmoremachine.share_inventory.desc.0", "打开催化剂槽", "Open Catalyst Slot")
		addCNEN(
			"gui.gtmoremachine.share_inventory.desc.1",
			"在催化剂槽中只有不消耗的物品才能参与合成。",
			"In the catalyst slot, only non-consumable items can participate in the synthesis.",
		)
		addCNEN(
			"gui.gtmoremachine.share_inventory.desc.2",
			"普通物品请通过在输入总线面前放置容器以自动输入。",
			"Common items can be automatically input by placing a container in front of the input bus.",
		)
		addCNEN("gui.gtmoremachine.me_export_buffer.item_status.full", "物品存储空间已满", "Item storage space is full.")
		addCNEN("gui.gtmoremachine.me_export_buffer.fluid_status.full", "流体存储空间已满", "Fluid storage space is full.")
		addCNEN("gtmoremachine.machine.wireless_monitor.tooltip.0", "所有者：%s", "Owner: %s")
		addCNEN("gtmoremachine.machine.wireless_monitor.tooltip.net_power", "平均净功率：%s", "Average Net Power: %s")
		addCNEN("gtmoremachine.machine.wireless_monitor.tooltip.net_cwu", "平均净CWU：%s", "Average Net CWU: %s")
		addCNEN(
			"gtmoremachine.machine.wireless_monitor.tooltip.statistics.energy",
			"用电统计：%s     显示格式：%s    功率状态：%s\n排序规则：%s        类别: %s",
			"Electricity Statistics：%s     Display Format：%s    Power status：%s\nSorting rules：%s        Type：%s",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_monitor.tooltip.statistics.cwu",
			"算力统计：%s     显示格式：%s    算力状态：%s",
			"CWU Statistics：%s     Display Format：%s    CWU status：%s",
		)
		addCNEN("gtmoremachine.machine.wireless_energy_hatch.tooltip.bind", "成功绑定至：%s", "Bind to: %s")
		addCNEN("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind", "解除绑定成功", "Unbind!")
		addCNEN("gtmoremachine.machine.wireless_energy_hatch.tooltip.1", "未绑定所有者", "No owner.")
		addCNEN("gtmoremachine.machine.wireless_energy_hatch.tooltip.2", "已绑定至：%s", "Bind to: %s")
		addCNEN("gtmoremachine.machine.wireless_energy_hatch.tooltip.3", "已绑定至未知用户：%s", "Bind to unknow user: %s")
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip",
			"监控无线能源的总量和使用量",
			"You can monitor the total energy and useage.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.1",
			"能源总量：%s EU (%s A %s§r)",
			"Total Energy: %s EU (%s A %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.2",
			"单次传输上限：%s EU/t (%s A %s§r)",
			"Single Transfer Limit：%s EU/t (%s A %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.last_minute",
			"近一分钟：§a%s EU/t (%s A %s§r)",
			"Last minute: §a%s EU/t (%s A %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.last_hour",
			"近一小时：§a%s EU/t (%s A %s§r)",
			"Last hour: §a%s EU/t (%s A %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.last_day",
			"近一天：§a%s EU/t (%s A %s§r)",
			"Last day: §a%s EU/t (%s A %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.now",
			"当前：§a%s EU/t (%s A %s§r)",
			"Now: §a%s EU/t (%sA %s§r)",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_monitor.tooltip.time_to_fill",
			"你一辈子都充不满",
			"You will never be satisfied with this for the rest of your life",
		)
		addCNEN("gtmoremachine.machine.wireless_cwu_monitor.tooltip.1", "CWU总量：%s CWU", "Total CWU: %s CWU")
		addCNEN(
			"gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_minute",
			"近一分钟：§a%s CWU/t",
			"Last minute: %s CWU",
		)
		addCNEN("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_hour", "近一小时：§a%s CWU/t", "Last hour: %s CWU")
		addCNEN("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_day", "近一天：§a%s CWU/t", "Last day: %s CWU")
		addCNEN("gtmoremachine.machine.wireless_cwu_monitor.tooltip.now", "当前：§a%s CWU/t", "Now: %s CWU")
		addCNEN(
			"gtmoremachine.machine.wireless_energy_interface.tooltip",
			"接收能量并发送至电网",
			"Receives energy and sends it to the power network",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_cover.tooltip.1",
			"无线能源接收器未绑定所有者",
			"The Wireless Energy Reciver unbind!",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_cover.tooltip.2",
			"无线能源接收器已绑定至：%s",
			"The Wireless Energy Reciver bind to: %s",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_cover.tooltip.3",
			"无线能源接收器已绑定至未知用户：%s",
			"The Wireless Energy Reciver bind to unknow user: %s",
		)
		addCNEN("gtmoremachine.machine.energy_hatch.input.tooltip", "为多方块结构输入能量", "Energy Input for Multiblocks")
		addCNEN("gtmoremachine.machine.energy_hatch.output.tooltip", "为多方块结构输出能量", "Energy Output for Multiblocks")
		addCNEN(
			"gtmoremachine.machine.energy_hatch.target.tooltip",
			"为多方块结构输入大量能量",
			"Large Amount Of Energy Input for Multiblocks",
		)
		addCNEN(
			"gtmoremachine.machine.energy_hatch.source.tooltip",
			"为多方块结构输出大量能量",
			"Large Amount Of Energy Output for Multiblocks",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_hatch.input.tooltip",
			"手持闪存右键点击能源仓可绑定·变更所有者，左键点击可解除绑定。",
			"You can bind or change the owner by left-click the Energy Hatch with Data Stick,or right-click to unbind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_hatch.output.tooltip",
			"手持闪存右键点击动力仓可绑定·变更所有者，左键点击可解除绑定。",
			"You can bind or change the owner by left-click the Dynoma Hatch with Data Stick,or right-click to unbind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_hatch.target.tooltip",
			"手持闪存右键点击激光靶仓可绑定·变更所有者，左键点击可解除绑定。",
			"You can bind or change the owner by left-click the Laser Target Hatch with Data Stick,or right-click to unbind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_energy_hatch.source.tooltip",
			"手持闪存右键点击激光源仓可绑定·变更所有者，左键点击可解除绑定。",
			"You can bind or change the owner by left-click the Laser Source Hatch with Data Stick,or right-click to unbind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.1",
			"从多方块结构输出算力数据",
			"Output computational power data from the multiblock structure.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.2",
			"需要使用闪存右键无线算力靶仓和无线算力源仓进行绑定。",
			"Need to bind the wireless computational power target Hatch and the wireless computational power source Hatch by right-clicking with a flash memory.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_transmitter_hatch.tobind",
			"源仓数据读取完成，请右键靶仓进行绑定。",
			"Source Hatch data reading completed, please right-click the target Hatch to bind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_receiver_hatch.tooltip.1",
			"为多方块结构输入算力数据",
			"Input computational power data for the multiblock structure",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_receiver_hatch.tooltip.2",
			"需要使用闪存右键无线算力靶仓和无线算力源仓进行绑定。",
			"Need to bind the wireless computational power target Hatch and the wireless computational power source Hatch by right-clicking with a flash memory.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_receiver_hatch.tobind",
			"靶仓数据读取完成，请右键源仓进行绑定。",
			"Target Hatch data reading completed, please right-click the source Hatch to bind.",
		)
		addCNEN(
			"gtmoremachine.machine.wireless_computation_hatch.binded",
			"无线算力数据仓绑定完成。",
			"Wireless computational power data Hatch binding completed.",
		)
		addCNEN(
			"gtmoremachine.machine.transmitter_hatch.bind",
			"已绑定到接收仓(%s)。",
			"Already bound to the receiving Hatch (%s).",
		)
		addCNEN("gtmoremachine.machine.transmitter_hatch.unbind", "未绑定到接收仓。", "Not bound to the receiving Hatch.")
		addCNEN("gtmoremachine.machine.receiver_hatch.bind", "已绑定到发射仓(%s)。", "Already bound to the launch Hatch (%s).")
		addCNEN("gtmoremachine.machine.receiver_hatch.unbind", "未绑定到发射仓。", "Not bound to the launch Hatch")
	}
}
