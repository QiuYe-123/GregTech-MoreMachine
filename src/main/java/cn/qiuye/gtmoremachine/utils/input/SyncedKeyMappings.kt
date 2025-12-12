package cn.qiuye.gtmoremachine.utils.input

import cn.qiuye.gtmoremachine.GTmm

import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModLoader

import java.util.function.Supplier

object SyncedKeyMappings {
    val VANILLA_JUMP: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyJump } }
    val VANILLA_SNEAK: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyShift } }
    val VANILLA_FORWARD: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyUp } }
    val VANILLA_BACKWARD: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyDown } }
    val VANILLA_LEFT: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyLeft } }
    val VANILLA_RIGHT: SyncedKeyMapping = SyncedKeyMapping
        .createFromMC { Supplier { Minecraft.getInstance().options.keyRight } }

    @JvmStatic
    fun init() {
        if (GTmm.isClientSide()) {
            MinecraftForge.EVENT_BUS.register(SyncedKeyMapping::class.java)
        }
        ModLoader.get().postEvent(SyncedKeyMappingEvent())
    }
}
