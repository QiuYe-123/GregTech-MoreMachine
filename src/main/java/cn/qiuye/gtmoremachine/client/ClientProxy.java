package cn.qiuye.gtmoremachine.client;

import cn.qiuye.gtmoremachine.GTmm;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ClientProxy {

    public static void init(IEventBus eventBus) {
        init();
        eventBus.addListener(ClientProxy::clientSetup);
        eventBus.addListener(ClientProxy::registerGuiOverlays);
    }

    private static void init() {}

    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(GTmm.id("hud"), new HudGuiOverlay());
    }

    private static void clientSetup(FMLClientSetupEvent event) {}
}
