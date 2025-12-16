package cn.qiuye.gtmoremachine.utils.input.open

import cn.qiuye.gtmoremachine.common.data.GTMMItems
import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMappings.OPEN_WET

import net.minecraft.world.InteractionHand

object HotKeyActions {

    @JvmStatic
    fun init() {
        OPEN_WET.registerGlobalListener { player, key, isDown ->
            if (isDown) {
                GTMMItems.WIRELESS_ENERGY_TERMINAL.asItem().use(player.level(), player, InteractionHand.MAIN_HAND)
            }
        }
    }
}
