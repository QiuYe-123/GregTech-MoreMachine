package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCN

object BlockLang {

	fun energyCommunicationUnit() {
		addCN("block.gtmoremachine.lv_energy_communication_unit", " 能源通讯单元 (§7LV§r)")
		addCN("block.gtmoremachine.mv_energy_communication_unit", "能源通讯单元 (§bMV§r)")
		addCN("block.gtmoremachine.hv_energy_communication_unit", "能源通讯单元 (§6HV§r)")
		addCN("block.gtmoremachine.ev_energy_communication_unit", "能源通讯单元 (§5EV§r)")
		addCN("block.gtmoremachine.iv_energy_communication_unit", "能源通讯单元 (§9IV§r)")
		addCN("block.gtmoremachine.luv_energy_communication_unit", "能源通讯单元 (§dLuV§r)")
		addCN("block.gtmoremachine.zpm_energy_communication_unit", "能源通讯单元 (§cZPM§r)")
		addCN("block.gtmoremachine.uv_energy_communication_unit", "能源通讯单元 (§3UV§r)")
		addCN("block.gtmoremachine.uhv_energy_communication_unit", "能源通讯单元 (§4UHV§r)")
		addCN("block.gtmoremachine.uev_energy_communication_unit", "能源通讯单元 (§aUEV§r)")
		addCN("block.gtmoremachine.uiv_energy_communication_unit", "能源通讯单元 (§2UIV§r)")
		addCN("block.gtmoremachine.uxv_energy_communication_unit", "能源通讯单元 (§eUXV§r)")
		addCN("block.gtmoremachine.opv_energy_communication_unit", "能源通讯单元 (§9§lOpV§r)")
		addCN("block.gtmoremachine.max_energy_communication_unit", "能源通讯单元 (§c§lMAX§r)")
	}

	fun blockinit() {
		energyCommunicationUnit()
	}

	fun init() {
		blockinit()
	}
}
