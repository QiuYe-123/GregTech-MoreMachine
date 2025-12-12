package cn.qiuye.gtmoremachine.utils.input

import net.minecraft.server.level.ServerPlayer

fun interface IKeyPressedListener {
    /**
     * Called **server-side only** when a player presses a specified keybinding.
     *
     * @param player     The player who pressed the key.
     * @param keyPressed The key the player pressed.
     */
    fun onKeyPressed(player: ServerPlayer, keyPressed: SyncedKeyMapping, isDown: Boolean)
}
