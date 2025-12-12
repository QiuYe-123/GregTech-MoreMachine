package cn.qiuye.gtmoremachine.client;

import cn.qiuye.gtmoremachine.common.CommonProxy;
import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMapping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
        init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ClientProxy::clientSetup);
    }

    private static void init() {}

    @SubscribeEvent
    public void registerKeyBindings(RegisterKeyMappingsEvent event) {
        SyncedKeyMapping.onRegisterKeyBinds(event);
    }

    @SubscribeEvent
    public void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("gtmmhud", new HudGuiOverlay());
    }

    private static void clientSetup(FMLClientSetupEvent event) {}
}
