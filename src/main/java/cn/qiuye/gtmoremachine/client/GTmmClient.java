package cn.qiuye.gtmoremachine.client;

import cn.qiuye.gtmoremachine.GTmm;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = GTmm.MOD_ID, dist = Dist.CLIENT)
public class GTmmClient {

    public GTmmClient(IEventBus modBus, FMLModContainer container) {
        ClientProxy.init(modBus);
    }
}
