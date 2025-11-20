package cn.qiuye.gtmoremachine.data;

import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.data.lang.LangHandler;

import com.tterrag.registrate.providers.ProviderType;

public class GTMMDatagen {

    public static void initPost() {
        GTMMRegistration.GTMMREGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
    }
}
