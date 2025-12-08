package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.config.GTMMConfig

import com.tterrag.registrate.providers.RegistrateLangProvider
import dev.toma.configuration.Configuration
import dev.toma.configuration.config.format.ConfigFormats
import dev.toma.configuration.config.value.ConfigValue
import dev.toma.configuration.config.value.ObjectValue

object ConfigurationLang {

    fun init(provider: RegistrateLangProvider) {
        dfs(
            provider,
            mutableSetOf(),
            Configuration.registerConfig(GTMMConfig::class.java, ConfigFormats.YAML).valueMap,
        )
    }

    private fun dfs(provider: RegistrateLangProvider, added: MutableSet<String>, map: Map<String, ConfigValue<*>>) {
        map.forEach { (_, value) ->
            val id = value.id
            if (added.add(id)) {
                provider.add("config.${GTmm.MOD_ID}.option.$id", id)
            }
            if (value is ObjectValue) {
                dfs(provider, added, value.get())
            }
        }
    }
}
