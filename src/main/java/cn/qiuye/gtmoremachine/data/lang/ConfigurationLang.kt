package cn.qiuye.gtmoremachine.data.lang

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.config.GTMMConfig
import cn.qiuye.gtmoremachine.data.lang.LangHandler.addEN

import dev.toma.configuration.Configuration
import dev.toma.configuration.config.format.ConfigFormats
import dev.toma.configuration.config.value.ConfigValue
import dev.toma.configuration.config.value.ObjectValue

object ConfigurationLang {

    fun init() {
        dfs(mutableSetOf(), Configuration.registerConfig(GTMMConfig::class.java, ConfigFormats.YAML).valueMap)
    }

    private fun dfs(added: MutableSet<String>, map: Map<String, ConfigValue<*>>) {
        map.forEach { (_, value) ->
            val id = value.id
            if (added.add(id)) {
                addEN("config.${GTmm.MOD_ID}.option.$id", id)
            }
            if (value is ObjectValue) {
                dfs(added, value.get())
            }
        }
    }
}
