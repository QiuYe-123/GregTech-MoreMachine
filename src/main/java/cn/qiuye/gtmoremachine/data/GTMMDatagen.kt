package cn.qiuye.gtmoremachine.data

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.lang.ChineseLangProvider
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM
import cn.qiuye.gtmoremachine.data.lang.LangHandler

import com.tterrag.registrate.providers.ProviderType

object GTMMDatagen {

	@JvmStatic
	fun initPost() {
		if (GTmm.isDataGen()) {
			GTMM.addDataGenerator(ProviderType.LANG, LangHandler::enInitialize)
			GTMM.addDataGenerator(ChineseLangProvider.LANG, LangHandler::cnInitialize)
		}
	}
}
