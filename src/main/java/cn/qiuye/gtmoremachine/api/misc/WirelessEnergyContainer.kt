package cn.qiuye.gtmoremachine.api.misc

import cn.qiuye.gtmoremachine.config.GTMMConfig
import cn.qiuye.gtmoremachine.data.wirelessenergy.WirelessEnergySavaedData
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils
import cn.qiuye.gtmoremachine.utils.TeamUtil

import com.gregtechceu.gtceu.api.machine.MetaMachine

import net.minecraft.core.GlobalPos
import net.minecraft.server.MinecraftServer

import java.math.BigInteger
import java.util.*
import kotlin.math.min

class WirelessEnergyContainer {

    var storage: BigInteger = BigInteger.ZERO
        set(value) {
            field = value
            WirelessEnergySavaedData.instance.isDirty = true
        }

    var rate: Long = 0
        set(value) {
            field = value
            WirelessEnergySavaedData.instance.isDirty = true
        }

    var bindPos: GlobalPos? = null
        set(value) {
            field = value
            WirelessEnergySavaedData.instance.isDirty = true
        }

    val uuid: UUID

    val energyStat: EnergyStat

    constructor(uuid: UUID, storage: BigInteger, rate: Long, bindPos: GlobalPos?) {
        this.storage = storage
        this.rate = rate
        this.bindPos = bindPos
        this.uuid = uuid
        this.energyStat = EnergyStat(0)
    }

    private constructor(uuid: UUID) {
        this.uuid = uuid
        this.storage = BigInteger.ZERO
        val currentTick = server!!.tickCount
        this.energyStat = EnergyStat(currentTick)
    }

    fun addEnergy(energy: Long, machine: MetaMachine?): Long {
        var change = energy
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) change = min(rate, energy)
        if (change <= 0) return 0
        storage = storage.add(BigInteger.valueOf(change))
        WirelessEnergySavaedData.instance.isDirty = true
        if (machine != null) {
            energyStat.update(BigInteger.valueOf(change), server!!.tickCount)
        }
        if (observed && machine != null) {
            TRANSFER_DATA[machine] = BasicTransferData(uuid, change, machine)
        }
        return change
    }

    fun removeEnergy(energy: Long, machine: MetaMachine?): Long {
        var change = BigIntegerUtils.getLongValue(storage).coerceAtMost(energy)
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) {
            change =
                BigIntegerUtils.getLongValue(storage).coerceAtMost(min(rate, energy))
        }
        if (change <= 0) return 0
        storage = storage.subtract(BigInteger.valueOf(change))
        WirelessEnergySavaedData.instance.isDirty = true
        if (machine != null) {
            energyStat.update(BigInteger.valueOf(change).negate(), server!!.tickCount)
        }
        if (observed && machine != null) {
            TRANSFER_DATA[machine] = BasicTransferData(uuid, -change, machine)
        }
        return change
    }

    fun getCapacity(): BigInteger? = null

    companion object {
        var observed: Boolean = false

        val TRANSFER_DATA: WeakHashMap<MetaMachine?, ITransferData?> = WeakHashMap<MetaMachine?, ITransferData?>()
        var server: MinecraftServer? = null

        fun getOrCreateContainer(uuid: UUID?): WirelessEnergyContainer =
            WirelessEnergySavaedData.instance.containerMap.computeIfAbsent(
                TeamUtil.getTeamUUID(uuid),
            ) { WirelessEnergyContainer(TeamUtil.getTeamUUID(uuid)) }
    }
}
