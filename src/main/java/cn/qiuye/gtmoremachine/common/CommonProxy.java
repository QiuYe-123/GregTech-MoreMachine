package cn.qiuye.gtmoremachine.common;

import cn.qiuye.gtmoremachine.GTmm;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        init();
    }

    private static void init() {
        GTmm.LOGGER.info("GTMoreMachine common proxy init!");
    }
}
