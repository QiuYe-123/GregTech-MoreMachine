package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object HUDLang {

	fun init() {
		addCNEN(
			"item.gtmoremachine.wireless_energy_terminal.hud.1",
			"全局平均：%s EU (%s A %s§r)",
			"Average Net ALL：%s EU (%s A %s§r)",
		)
		addCNEN(
			"item.gtmoremachine.wireless_energy_terminal.hud.2",
			"输入平均：%s EU (%s A %s§r)",
			"Average Net IN：%s EU (%s A %s§r)",
		)
		addCNEN(
			"item.gtmoremachine.wireless_energy_terminal.hud.3",
			"输出平均：%s EU (%s A %s§r)",
			"Average Net OUT：%s EU (%s A %s§r)",
		)
		addCNEN(
			"item.gtmoremachine.wireless_energy_terminal.hud.4",
			"能源总量：%s EU (%s A %s§r)",
			"Total Energy：%s EU (%s A %s§r)",
		)
	}
}
