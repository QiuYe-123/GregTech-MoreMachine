package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import cn.qiuye.gtmoremachine.api.misc.wireless.time.TimeStat;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.data.wireless.energy.WirelessEnergySavaedData;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.UUID;
import java.util.WeakHashMap;

@Getter
public class WirelessEnergyContainer {

    public static boolean observed;

    public static final WeakHashMap<MetaMachine, ITransferData> TRANSFER_DATA = new WeakHashMap<>();
    public static MinecraftServer server;

    public static WirelessEnergyContainer getOrCreateContainer(UUID uuid) {
        return WirelessEnergySavaedData.INSTANCE.containerMap.computeIfAbsent(TeamUtils.getTeamUUID(uuid), WirelessEnergyContainer::new);
    }

    private BigInteger storage;

    private long rate;

    private GlobalPos bindPos;

    private final UUID uuid;

    private final TimeStat allEnergyStat;

    private final TimeStat inEnergyStat;

    private final TimeStat outEnergyStat;

    public WirelessEnergyContainer(UUID uuid, BigInteger storage, long rate, GlobalPos bindPos) {
        this.storage = storage;
        this.rate = rate;
        this.bindPos = bindPos;
        this.uuid = uuid;
        this.allEnergyStat = new TimeStat(0);
        this.inEnergyStat = new TimeStat(0);
        this.outEnergyStat = new TimeStat(0);
    }

    private WirelessEnergyContainer(UUID uuid) {
        this.uuid = uuid;
        this.storage = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.allEnergyStat = new TimeStat(currentTick);
        this.inEnergyStat = new TimeStat(currentTick);
        this.outEnergyStat = new TimeStat(currentTick);
    }

    public long addEnergy(long energy, @Nullable MetaMachine machine) {
        long change = energy;
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) change = Math.min(rate, energy);
        if (change <= 0) return 0;
        storage = storage.add(BigInteger.valueOf(change));
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
            inEnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, new BigInteger(String.valueOf(change)), machine));
        }
        return change;
    }

    public long removeEnergy(long energy, @Nullable MetaMachine machine) {
        long change = Math.min(BigIntegerUtils.getLongValue(storage), energy);
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(storage), Math.min(rate, energy));
        if (change <= 0) return 0;
        storage = storage.subtract(BigInteger.valueOf(change));
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
            outEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, new BigInteger(String.valueOf(-change)), machine));
        }
        return change;
    }

    public BigInteger addEnergy(BigInteger change, @Nullable MetaMachine machine) {
        if (change.compareTo(BigInteger.ZERO) <= 0) return BigInteger.ZERO;
        storage = storage.add(change);
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(change, server.getTickCount());
            inEnergyStat.update(change, server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, change, machine));
        }
        return change;
    }

    public BigInteger removeEnergy(BigInteger energy, @Nullable MetaMachine machine) {
        BigInteger change = storage.min(energy);
        if (change.compareTo(BigInteger.ZERO) <= 0) return BigInteger.ZERO;
        storage = storage.subtract(change);
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(change.negate(), server.getTickCount());
            outEnergyStat.update(change.negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, change.negate(), machine));
        }
        return change;
    }

    public void setStorage(BigInteger energy) {
        storage = energy;
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
    }

    public void setRate(long rate) {
        this.rate = rate;
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
    }

    public void setBindPos(GlobalPos bindPos) {
        this.bindPos = bindPos;
        WirelessEnergySavaedData.INSTANCE.setDirty(true);
    }

    public BigInteger getCapacity() {
        return null;
    }
}
