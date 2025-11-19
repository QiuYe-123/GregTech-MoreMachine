package cn.qiuye.gtmoremachine.forge

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.misc.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior
import cn.qiuye.gtmoremachine.data.wirelessenergy.WirelessEnergySavaedData

import net.minecraft.core.GlobalPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = GTmm.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
object ForgeCommonEventListener {

    @SubscribeEvent
    fun onServerTickEvent(event: ServerTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.server.tickCount % 20 == 0) {
                val refreshBinding = event.server.tickCount % 200 == 0
                for (container in WirelessEnergySavaedData.instance.containerMap.values) {
                    if (refreshBinding) {
                        var rate: Long = 0
                        val pos: GlobalPos? = container.bindPos
                        if (pos != null) {
                            rate = WirelessEnergyBindingToolBehavior.getRate(
                                event.server.getLevel(pos.dimension()),
                                pos.pos(),
                            )
                        }
                        container.rate = rate
                    }
                    container.energyStat.tick()
                }
            }
        } else {
            WirelessEnergyContainer.observed = false
        }
    }

    @SubscribeEvent
    fun serverSetup(event: LevelEvent.Load) {
        if (event.level is ServerLevel) {
            val serverLevel: ServerLevel = event.level.server?.getLevel(Level.OVERWORLD) ?: return
            WirelessEnergySavaedData.instance = WirelessEnergySavaedData.getOrCreate(serverLevel)
            WirelessEnergyContainer.server = event.level.server
        }
    }
}
