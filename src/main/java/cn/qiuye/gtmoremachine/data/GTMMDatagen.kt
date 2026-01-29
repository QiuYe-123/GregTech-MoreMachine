package cn.qiuye.gtmoremachine.data

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM
import cn.qiuye.gtmoremachine.api.lang.SimplifiedChineseLanguageProvider
import cn.qiuye.gtmoremachine.api.lang.TraditionalChineseLanguageProvider
import cn.qiuye.gtmoremachine.data.lang.LangHandler

import com.tterrag.registrate.providers.ProviderType

object GTMMDatagen {

    @JvmStatic
    fun initPost() {
        if (GTmm.isDataGen()) {
            GTMM.addDataGenerator(ProviderType.LANG, LangHandler::enInitialize)
            GTMM.addDataGenerator(
                SimplifiedChineseLanguageProvider.LANG,
                LangHandler::cnInitialize,
            )
            GTMM.addDataGenerator(
                TraditionalChineseLanguageProvider.LANG,
                LangHandler::twInitialize,
            )
        }
    }
}
