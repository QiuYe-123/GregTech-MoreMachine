package cn.qiuye.gtmoremachine.client;

import cn.qiuye.gtmoremachine.common.CommonProxy;
import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMapping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
        init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ClientProxy::clientSetup);
        eventBus.addListener(ClientProxy::registerKeyBindings);
        eventBus.addListener(ClientProxy::registerGuiOverlays);
    }

    private static void init() {}

    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        SyncedKeyMapping.onRegisterKeyBinds(event);
    }

    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("hud", new GTmmHudGuiOverlay());
    }

    private static void clientSetup(FMLClientSetupEvent event) {}
}
