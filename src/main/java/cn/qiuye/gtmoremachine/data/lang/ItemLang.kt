package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCN
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object ItemLang {

	fun programmablec() {
		addCN("item.gtmoremachine.programmable_cover", "可编程覆盖板")
		addCN("item.gtmoremachine.virtual_item_provider", "虚拟物品提供器")
		addCN("item.gtmoremachine.virtual_item_provider_cell", "虚拟物品提供器元件")
	}

	fun itme() {
		addCN("item.gtmoremachine.advanced_terminal", "§b高级终端")
	}

	fun init() {
		programmablec()
		itme()
		addCNEN(
			"item.gtmoremachine.wireless_energy_receive_cover.tooltip.1",
			"§7作§f覆盖板§7时从电网§b拉取能量§7传输到机器。",
			"§bPull Energy§7 from EU network to the machine as §fCover§7.",
		)
		addCNEN(
			"item.gtmoremachine.wireless_energy_receive_cover.tooltip.2",
			"§7只可用于§e单方块机器§7。无法将超过机器电压等级的覆盖板安装到机器上。",
			"§7Can only used for §esingle block machine§7.Can't put on the machine blow the cover's voltage",
		)
		addCNEN(
			"item.gtmoremachine.wireless_energy_receive_cover.tooltip.3",
			"§b能量传输效率：§f%s §7EU/t",
			"§bEnergy transfer speed: §f%s §7EU/t",
		)
		addCNEN("item.gtmoremachine.wireless_transfer.tooltip.1", "§7已绑定容器：§f%s (%s)", "§7Bind to: §f%s (%s)")
		addCNEN(
			"item.gtmoremachine.wireless_transfer.tooltip.2",
			"§7潜行右键需要绑定的容器来进行绑定。潜行右键空气取消绑定。",
			"§7Right click the container with shift to bind container.Right click the air with shift to unbind.",
		)
		addCNEN("item.gtmoremachine.wireless_transfer.tooltip.bind.1", "绑定容器成功：%s (%s)", "Success bind to: %s (%s)")
		addCNEN("item.gtmoremachine.wireless_transfer.tooltip.bind.2", "解除绑定成功", "Success unbind.")
		addCNEN(
			"item.gtmoremachine.wireless_transfer.item.tooltip.1",
			"§7作§f覆盖板§7时从机器中§b提取物品§7到§e绑定的容器§7中。",
			"§bTransfer Item§7 to §ebinded container§7 from the machine as §fCover§7.",
		)
		addCNEN(
			"item.gtmoremachine.wireless_transfer.fluid.tooltip.1",
			"§7作§f覆盖板§7时从机器中§b提取流体§7到§e绑定的容器§7中。",
			"§bTransfer Fluid§7 to §ebinded container§7 from the machine as §fCover§7.",
		)
		addCNEN("item.gtmoremachine.advanced_wireless_transfer.tooltip.1", "§7可使用§f过滤卡", "§7Can use §f filter card")
	}
}
