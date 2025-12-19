package cn.qiuye.gtmoremachine.utils.input

import cn.qiuye.gtmoremachine.GTmm

import com.gregtechceu.gtceu.utils.input.SyncedKeyMapping

import net.minecraftforge.client.settings.KeyConflictContext

import com.mojang.blaze3d.platform.InputConstants

object SyncedKeyMappings {
    val OPEN_WET: SyncedKeyMapping = SyncedKeyMapping.createConfigurable(
        "gtmoremachine.key",
        KeyConflictContext.IN_GAME,
        InputConstants.CURSOR_DISABLED,
        GTmm.MOD_NAME,
    )

    @JvmStatic
    fun init() {}
}
