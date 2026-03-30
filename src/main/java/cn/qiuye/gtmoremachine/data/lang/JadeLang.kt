package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.data.lang.LangHandler.addCNEN

object JadeLang {

	fun init() {
		addCNEN(
			"config.jade.plugin_gtmoremachine.wireless_energy_provider",
			"[GTMoreMachine] 无线能源监视器",
			"[GTMoreMachine] Wireless Energy Monitor",
		)
		addCNEN(
			"config.jade.plugin_gtmoremachine.wireless_optical_computation_hatch_provider",
			"[GTMoreMachine] 无线光学算力仓",
			"[GTMoreMachine] Wireless OpticalComputation",
		)
		addCNEN(
			"config.jade.plugin_gtmoremachine.wireless_cwu_provider",
			"[GTMoreMachine] 无线CWU监视器",
			"[GTMoreMachine] Wireless CWU Monitor",
		)
		addCNEN(
			"config.jade.plugin_gtmoremachine.wireless_energy_hatch_provider.tooltip.1",
			"能源总量: %s EU (%s A %s§r)",
			"Total Energy: %s EU (%s A %s§r)",
		)
		addCNEN(
			"config.jade.plugin_gtmoremachine.wireless_cwu_hatch_provider.tooltip.1",
			"CWU总量：%s CWU",
			"Total CWU: %s CWU",
		)
	}
}
