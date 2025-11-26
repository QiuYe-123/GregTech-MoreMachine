package cn.qiuye.gtmoremachine.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler {

    public static void init(RegistrateLangProvider provider) {
        ConfigurationLang.init(provider);
        ItemLang.init(provider);
        MachineLang.init(provider);
        CreativeLang.init(provider);
        AdvancedTerminalLang.init(provider);
        JadeLang.init(provider);
    }
}
