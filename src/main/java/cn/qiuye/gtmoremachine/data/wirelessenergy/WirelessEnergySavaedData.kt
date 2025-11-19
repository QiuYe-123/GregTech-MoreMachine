package cn.qiuye.gtmoremachine.data.wirelessenergy

import cn.qiuye.gtmoremachine.api.misc.WirelessEnergyContainer

import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData

import java.math.BigInteger
import java.util.*

open class WirelessEnergySavaedData : SavedData {
    val containerMap: MutableMap<UUID, WirelessEnergyContainer> = HashMap<UUID, WirelessEnergyContainer>()

    constructor()

    constructor(tag: CompoundTag) {
        val allEnergy = tag.getList("allEnergy", Tag.TAG_COMPOUND.toInt())
        for (i in allEnergy.indices) {
            val container: WirelessEnergyContainer = readTag(allEnergy.getCompound(i))
            containerMap[container.uuid] = container
        }
    }

    override fun save(compoundTag: CompoundTag): CompoundTag {
        val allEnergy = ListTag()
        for (container in containerMap.values) {
            val engTag = toTag(container)
            if (engTag.isEmpty) continue
            allEnergy.add(engTag)
        }
        compoundTag.put("allEnergy", allEnergy)
        return compoundTag
    }

    protected fun readTag(engTag: CompoundTag): WirelessEnergyContainer {
        val uuid = engTag.getUUID("uuid")
        val en = engTag.getString("energy")
        val energy = BigInteger(en.ifEmpty { "0" })
        val rate = engTag.getLong("rate")
        val bindPos = readGlobalPos(engTag.getString("dimension"), engTag.getLong("pos"))
        return WirelessEnergyContainer(uuid, energy, rate, bindPos)
    }

    protected fun toTag(container: WirelessEnergyContainer): CompoundTag {
        val engTag = CompoundTag()
        val storage: BigInteger = container.storage
        if (storage != BigInteger.ZERO) {
            engTag.putString("energy", storage.toString())
        }
        val rate: Long = container.rate
        if (rate != 0L) {
            engTag.putLong("rate", rate)
        }
        val bindPos: GlobalPos? = container.bindPos
        if (bindPos != null) {
            engTag.putString("dimension", bindPos.dimension().location().toString())
            engTag.putLong("pos", bindPos.pos().asLong())
        }
        if (!engTag.isEmpty) engTag.putUUID("uuid", container.uuid)
        return engTag
    }

    companion object {
        lateinit var instance: WirelessEnergySavaedData

        fun getOrCreate(serverLevel: ServerLevel): WirelessEnergySavaedData = serverLevel.dataStorage
            .computeIfAbsent(
                { tag: CompoundTag? -> WirelessEnergySavaedData(tag!!) },
                { WirelessEnergySavaedData() },
                "gtceu_wireless_energy",
            )

        private fun readGlobalPos(dimension: String, pos: Long): GlobalPos? {
            if (dimension.isEmpty()) return null
            if (pos == 0L) return null
            val key = ResourceLocation.tryParse(dimension) ?: return null
            return GlobalPos.of(ResourceKey.create(Registries.DIMENSION, key), BlockPos.of(pos))
        }
    }
}
