package cn.qiuye.gtmoremachine.forge;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.data.wireless.cwu.WirelessCWUSavedData;
import cn.qiuye.gtmoremachine.data.wireless.energy.WirelessEnergySavedData;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigInteger;

@Mod.EventBusSubscriber(modid = GTmm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEventListener {

    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.getServer().getTickCount() % 20 == 0) {
                boolean refreshBinding = event.getServer().getTickCount() % 200 == 0;
                for (WirelessEnergyContainer container : WirelessEnergySavedData.INSTANCE.containerMap.values()) {
                    if (refreshBinding) {
                        BigInteger rate = BigInteger.ZERO;
                        GlobalPos pos = container.getBindPos();
                        if (pos != null) {
                            rate = WirelessEnergyBindingToolBehavior.Companion.getRate(event.getServer().getLevel(pos.dimension()), pos.pos());
                        }
                        container.setRate(rate);
	                    if (pos != null) {
		                    container.setDimensional(14, rate.compareTo(BigInteger.ZERO) >= 0, MetaMachine.getMachine(event.getServer().getLevel(pos.dimension()), pos.pos()));
	                    }
                    }
                    container.getAllEnergyStat().tick();
                    container.getInEnergyStat().tick();
                    container.getOutEnergyStat().tick();
                }
                for (WirelessCWUContainer container : WirelessCWUSavedData.INSTANCE.containerMap.values()) {
                    container.getAllCWUStat().tick();
                    container.getInCWUStat().tick();
                    container.getOutCWUStat().tick();
                }
            }
        } else {
            WirelessEnergyContainer.observed = false;
            WirelessCWUContainer.observed = false;
        }
    }

    @SubscribeEvent
    public static void serverSetup(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevel serverLevel = level.getServer().getLevel(Level.OVERWORLD);
            if (serverLevel == null) return;
            WirelessEnergySavedData.INSTANCE = WirelessEnergySavedData.getOrCreate(serverLevel);
            WirelessEnergyContainer.server = event.getLevel().getServer();
            WirelessCWUSavedData.INSTANCE = WirelessCWUSavedData.getOrCreate(serverLevel);
            WirelessCWUContainer.server = event.getLevel().getServer();
        }
    }
}
