package cn.qiuye.gtmoremachine.data

import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration
import cn.qiuye.gtmoremachine.data.lang.LangHandler

import com.tterrag.registrate.providers.ProviderType
import com.tterrag.registrate.providers.RegistrateLangProvider
import com.tterrag.registrate.util.nullness.NonNullConsumer

object GTMMDatagen {

    @JvmStatic
    fun initPost() {
        GTMMRegistration.GTMMREGISTRATE.addDataGenerator(
            ProviderType.LANG,
            NonNullConsumer { provider: RegistrateLangProvider -> LangHandler.init(provider) },
        )
    }
}
