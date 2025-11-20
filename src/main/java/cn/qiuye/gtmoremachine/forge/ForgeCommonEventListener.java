package cn.qiuye.gtmoremachine.forge;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.misc.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.data.wirelessenergy.WirelessEnergySavaedData;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GTmm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEventListener {

    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.getServer().getTickCount() % 20 == 0) {
                boolean refreshBinding = event.getServer().getTickCount() % 200 == 0;
                for (WirelessEnergyContainer container : WirelessEnergySavaedData.INSTANCE.containerMap.values()) {
                    if (refreshBinding) {
                        long rate = 0;
                        GlobalPos pos = container.getBindPos();
                        if (pos != null) {
                            rate = WirelessEnergyBindingToolBehavior.getRate(event.getServer().getLevel(pos.dimension()), pos.pos());
                        }
                        container.setRate(rate);
                    }
                    container.getEnergyStat().tick();
                }
            }
        } else {
            WirelessEnergyContainer.observed = false;
        }
    }

    @SubscribeEvent
    public static void serverSetup(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevel serverLevel = level.getServer().getLevel(Level.OVERWORLD);
            if (serverLevel == null) return;
            WirelessEnergySavaedData.INSTANCE = WirelessEnergySavaedData.getOrCreate(serverLevel);
            WirelessEnergyContainer.server = event.getLevel().getServer();
        }
    }
}
