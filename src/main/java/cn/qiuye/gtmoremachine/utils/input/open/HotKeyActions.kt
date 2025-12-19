package cn.qiuye.gtmoremachine.utils.input.open

import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMappings.OPEN_WET

object HotKeyActions {

    @JvmStatic
    fun init() {
        OPEN_WET.registerGlobalListener { player, key, isDown ->
            if (isDown) { }
        }
    }
}
