package cn.qiuye.gtmoremachine.api

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage

@GTMMDataGeneratorScanned
object GTMMValues {

	@GTMMRegisterLanguage(cn = "已启用", en = "Enabled")
	const val ENABLED = "gtmoremachine.value.boolean.enabled"

	@GTMMRegisterLanguage(cn = "已禁用", en = "Disabled")
	const val DISABLED = "gtmoremachine.value.boolean.DISABLED"

	const val ADVTER_VALUE_PREFIX = "gtmoremachine.adv_terminal."

	const val MODID_APPENG = "ae2"
	const val MODID_FTB_TEAMS = "ftbteams"
	const val MODID_CURIOS = "curios"
}
