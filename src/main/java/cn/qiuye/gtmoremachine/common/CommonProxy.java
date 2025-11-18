package cn.qiuye.gtmoremachine.common;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;

import cn.qiuye.gtmoremachine.config.GTMMConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {

    public CommonProxy() {
        init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GTMMRegistration.GTMMREGISTRATE.registerEventListeners(eventBus);
    }

    private static void init() {
        GTmm.LOGGER.info("GTMoreMachine common proxy init!");
        GTMMConfig.init();
    }
}
