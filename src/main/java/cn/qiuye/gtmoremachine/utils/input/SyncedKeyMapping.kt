package cn.qiuye.gtmoremachine.utils.input

import cn.qiuye.gtmoremachine.GTmm

import com.gregtechceu.gtceu.common.network.GTNetwork
import com.gregtechceu.gtceu.common.network.packets.CPacketKeyDown

import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.settings.IKeyConflictContext
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

import com.mojang.blaze3d.platform.InputConstants
import it.unimi.dsi.fastutil.ints.Int2BooleanMap
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.jetbrains.annotations.ApiStatus

import java.util.*
import java.util.function.Supplier

class SyncedKeyMapping {
    @OnlyIn(Dist.CLIENT)
    private var keyMapping: KeyMapping? = null

    @OnlyIn(Dist.CLIENT)
    private var keyMappingGetter: Supplier<Supplier<KeyMapping?>?>? = null
    private val needsRegister: Boolean

    @OnlyIn(Dist.CLIENT)
    private var keyCode = 0

    @OnlyIn(Dist.CLIENT)
    private var isKeyDown = false

    private val serverMapping = WeakHashMap<ServerPlayer, Boolean>()
    private val playerListeners = WeakHashMap<ServerPlayer, MutableSet<IKeyPressedListener>>()
    private val globalListeners =
        Collections.newSetFromMap<IKeyPressedListener>(WeakHashMap())

    private constructor(mcKeyMapping: Supplier<Supplier<KeyMapping?>?>?) {
        if (GTmm.isClientSide()) {
            this.keyMappingGetter = mcKeyMapping
        }
        // Does not need to be registered, will be registered by MC
        this.needsRegister = false

        KEYMAPPINGS.put(syncIndex++, this)
    }

    private constructor(keyCode: Int) {
        if (GTmm.isClientSide() && !GTmm.isDataGen()) {
            this.keyCode = keyCode
        }
        // Does not need to be registered, is not a configurable key mapping
        this.needsRegister = false

        KEYMAPPINGS.put(syncIndex++, this)
    }

    private constructor(nameKey: String, ctx: IKeyConflictContext, keyCode: Int, category: String) {
        if (GTmm.isClientSide() && !GTmm.isDataGen()) {
            this.keyMapping = createKeyMapping(nameKey, ctx, keyCode, category) as KeyMapping
        }
        this.needsRegister = true
        KEYMAPPINGS.put(syncIndex++, this)
    }

    @OnlyIn(Dist.CLIENT)
    private fun createKeyMapping(nameKey: String, ctx: IKeyConflictContext, keyCode: Int, category: String): Any =
        KeyMapping(nameKey, ctx, InputConstants.Type.KEYSYM, keyCode, category)

    fun isKeyDown(player: Player): Boolean {
        if (player.level().isClientSide) {
            if (keyMapping != null) {
                return keyMapping!!.isDown()
            }
            val id = Minecraft.getInstance().window.window
            return InputConstants.isKeyDown(id, keyCode)
        }
        val isKeyDown = serverMapping[player as ServerPlayer]
        return isKeyDown ?: false
    }

    fun registerPlayerListener(player: ServerPlayer, listener: IKeyPressedListener): SyncedKeyMapping {
        val listenerSet: MutableSet<IKeyPressedListener>? = playerListeners
            .computeIfAbsent(player) { _: ServerPlayer -> Collections.newSetFromMap(WeakHashMap()) }
        listenerSet?.add(listener)
        return this
    }

    fun removePlayerListener(player: ServerPlayer, listener: IKeyPressedListener) {
        val listenerSet: MutableSet<IKeyPressedListener>? = playerListeners[player]
        listenerSet?.remove(listener)
    }

    fun registerGlobalListener(listener: IKeyPressedListener?): SyncedKeyMapping {
        globalListeners.add(listener!!)
        return this
    }

    fun removeGlobalListener(listener: IKeyPressedListener) {
        globalListeners.remove(listener)
    }

    @ApiStatus.Internal
    fun serverActivate(keyDown: Boolean, player: ServerPlayer) {
        this.serverMapping[player] = keyDown

        // Player listeners
        val listenerSet = playerListeners[player]
        if (!listenerSet.isNullOrEmpty()) {
            for (listener in listenerSet) {
                listener.onKeyPressed(player, this, keyDown)
            }
        }
        // Global listeners
        for (listener in globalListeners) {
            listener.onKeyPressed(player, this, keyDown)
        }
    }

    companion object {
        private val KEYMAPPINGS: Int2ObjectMap<SyncedKeyMapping> = Int2ObjectOpenHashMap()
        private var syncIndex = 0

        private val updatingKeyDown: Int2BooleanMap = Int2BooleanOpenHashMap()

        fun createFromMC(mcKeyMapping: Supplier<Supplier<KeyMapping?>?>?): SyncedKeyMapping =
            SyncedKeyMapping(mcKeyMapping)

        fun create(keyCode: Int): SyncedKeyMapping = SyncedKeyMapping(keyCode)

        @JvmOverloads
        fun createConfigurable(
            nameKey: String,
            ctx: IKeyConflictContext,
            keyCode: Int,
            category: String = GTmm.MOD_NAME,
        ): SyncedKeyMapping = SyncedKeyMapping(nameKey, ctx, keyCode, category)

        fun onRegisterKeyBinds(event: RegisterKeyMappingsEvent) {
            for (value in KEYMAPPINGS.values) {
                if (value.keyMappingGetter != null) {
                    value.keyMapping = value.keyMappingGetter!!.get()!!.get()
                    value.keyMappingGetter = null
                }
                value.keyMapping?.let {
                    if (value.needsRegister) {
                        event.register(it)
                    }
                }
            }
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        fun onClientTick(event: TickEvent.ClientTickEvent) {
            if (event.phase == TickEvent.Phase.START) {
                updatingKeyDown.clear()
                for (entry in KEYMAPPINGS.int2ObjectEntrySet()) {
                    val keyMapping = entry.value
                    val previousKeyDown = keyMapping.isKeyDown

                    if (keyMapping.keyMapping != null) {
                        keyMapping.isKeyDown = keyMapping.keyMapping!!.isDown()
                    } else {
                        val id = Minecraft.getInstance().window.window
                        keyMapping.isKeyDown = InputConstants.isKeyDown(id, keyMapping.keyCode)
                    }

                    if (previousKeyDown != keyMapping.isKeyDown) {
                        updatingKeyDown.put(entry.intKey, keyMapping.isKeyDown)
                    }
                }
                if (!updatingKeyDown.isEmpty()) {
                    GTNetwork.sendToServer(CPacketKeyDown(updatingKeyDown))
                }
            }
        }

        @ApiStatus.Internal
        fun getFromSyncId(id: Int): SyncedKeyMapping? = KEYMAPPINGS.get(id)
    }
}
