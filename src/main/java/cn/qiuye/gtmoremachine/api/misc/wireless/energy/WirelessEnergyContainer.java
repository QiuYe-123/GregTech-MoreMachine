package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import cn.qiuye.gtmoremachine.api.misc.wireless.time.TimeStat;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.data.wireless.energy.WirelessEnergySavedData;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.UUID;
import java.util.WeakHashMap;

@Getter
public class WirelessEnergyContainer {

    public static boolean observed;

    public static final WeakHashMap<MetaMachine, ITransferData> TRANSFER_DATA = new WeakHashMap<>();
    public static final WeakHashMap<Level, IDimensionTransferData> DIMENSIONAL_TRANSFER_DATA = new WeakHashMap<>();
    public static final WeakHashMap<MetaMachine, ICapacitylimitData> CAPACITY_STORAGE_DATA = new WeakHashMap<>();
    public static MinecraftServer server;

    public static WirelessEnergyContainer getOrCreateContainer(UUID uuid) {
        return WirelessEnergySavedData.INSTANCE.containerMap.computeIfAbsent(TeamUtils.getTeamUUID(uuid), WirelessEnergyContainer::new);
    }

    private BigInteger storage;

    private BigInteger rate;

    private GlobalPos bindPos;

    private BigInteger capacity;

    private BigInteger passiveDrain;

    private final UUID uuid;

    private final TimeStat allEnergyStat;

    private final TimeStat inEnergyStat;

    private final TimeStat outEnergyStat;

    public WirelessEnergyContainer(UUID uuid, BigInteger storage, BigInteger rate, BigInteger capacity, BigInteger passiveDrain, GlobalPos bindPos) {
        this.storage = storage;
        this.rate = rate;
        this.bindPos = bindPos;
        this.capacity = capacity;
        this.passiveDrain = passiveDrain;
        this.uuid = uuid;
        this.allEnergyStat = new TimeStat(0);
        this.inEnergyStat = new TimeStat(0);
        this.outEnergyStat = new TimeStat(0);
    }

    private WirelessEnergyContainer(UUID uuid) {
        this.uuid = uuid;
        this.storage = BigInteger.ZERO;
        this.rate = BigInteger.ZERO;
        this.capacity = BigInteger.ZERO;
        this.passiveDrain = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.allEnergyStat = new TimeStat(currentTick);
        this.inEnergyStat = new TimeStat(currentTick);
        this.outEnergyStat = new TimeStat(currentTick);
    }

    public long addEnergy(int tier, long energy, @Nullable MetaMachine machine) {
        long change = energy;
        if (GTMMConfig.getINSTANCE().isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(rate), energy);
        if (GTMMConfig.getINSTANCE().isWirelessDimensionRateEnable && isDimensionBound(tier, machine)) return 0;
        if (GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable && storage.add(BigInteger.valueOf(change)).compareTo(capacity) > 0) change = BigIntegerUtils.getLongValue(capacity.subtract(storage));
        if (change <= 0) return 0;
        storage = storage.add(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
            inEnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, new BigInteger(String.valueOf(change)), machine));
        }
        return change;
    }

    public long removeEnergy(int tier, long energy, @Nullable MetaMachine machine) {
        long change = Math.min(BigIntegerUtils.getLongValue(storage), energy);
        if (GTMMConfig.getINSTANCE().isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(storage), Math.min(BigIntegerUtils.getLongValue(rate), energy));
        if (GTMMConfig.getINSTANCE().isWirelessDimensionRateEnable && isDimensionBound(tier, machine)) return 0;
        if (change <= 0) return 0;
        storage = storage.subtract(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
            outEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, new BigInteger(String.valueOf(-change)), machine));
        }
        return change;
    }

    public long addEnergy(long energy, @Nullable MetaMachine machine) {
        long change = energy;
        if (GTMMConfig.getINSTANCE().isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(rate), energy);
        if (GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable && storage.add(BigInteger.valueOf(change)).compareTo(capacity) > 0) change = BigIntegerUtils.getLongValue(capacity.subtract(storage));
        if (change <= 0) return 0;
        storage = storage.add(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
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
        if (GTMMConfig.getINSTANCE().isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(storage), Math.min(BigIntegerUtils.getLongValue(rate), energy));
        if (change <= 0) return 0;
        storage = storage.subtract(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
            outEnergyStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, new BigInteger(String.valueOf(-change)), machine));
        }
        return change;
    }

    public BigInteger addEnergy(BigInteger energy, @Nullable MetaMachine machine) {
        BigInteger change = energy;
        if (GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable && storage.add(change).compareTo(capacity) > 0) change = capacity.subtract(storage);
        if (change.compareTo(BigInteger.ZERO) <= 0) return BigInteger.ZERO;
        storage = storage.add(change);
        WirelessEnergySavedData.INSTANCE.setDirty(true);
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
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            allEnergyStat.update(change.negate(), server.getTickCount());
            outEnergyStat.update(change.negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(uuid, change.negate(), machine));
        }
        return change;
    }

    public void PassiveDrainEnergy(BigInteger energy) {
        BigInteger change = storage.min(energy);
        if (change.compareTo(BigInteger.ZERO) <= 0) return;
        storage = storage.subtract(change);
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        allEnergyStat.update(change.negate(), server.getTickCount());
        outEnergyStat.update(change.negate(), server.getTickCount());
    }

    public void setStorage(BigInteger energy) {
        storage = energy;
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    public void setRate(BigInteger rate) {
        this.rate = rate;
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    public void setBindPos(GlobalPos bindPos) {
        this.bindPos = bindPos;
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    public StoragePercentageData getStoragePercentage() {
        var storage = new BigDecimal(this.storage);
        var capacity = new BigDecimal(this.capacity);
        return new StoragePercentageData(storage.divide(capacity, MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100)), this.storage, this.capacity);
    }

    public void setCapacity(BigInteger StorageCapacity, BigInteger PassiveDrain, boolean Bind, MetaMachine machine) {
        if (Bind) {
            if (machine != null) CAPACITY_STORAGE_DATA.put(machine, new CapacityStorageData(uuid, StorageCapacity, PassiveDrain, machine));
        } else {
            if (machine != null) CAPACITY_STORAGE_DATA.remove(machine);
        }
        BigInteger change = BigInteger.ZERO;
        BigInteger passiveDrain = BigInteger.ZERO;
        for (ICapacitylimitData data : CAPACITY_STORAGE_DATA.values()) {
            if (data.StorageCapacity() != null) change = change.add(data.StorageCapacity());
            if (data.PassiveDrain() != null) passiveDrain = passiveDrain.add(data.PassiveDrain());
        }
        this.capacity = change;
        this.passiveDrain = passiveDrain;
        if (this.storage.compareTo(this.capacity) > 0) this.storage = this.capacity;
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    public void setDimensional(int Voltagelevel, boolean Bind, MetaMachine machine) {
        if (machine == null) return;
        if (Bind) {
            DIMENSIONAL_TRANSFER_DATA.put(machine.getLevel(), new DimensionBoundData(uuid, machine.getLevel(), Voltagelevel, machine));
        } else {
            DIMENSIONAL_TRANSFER_DATA.remove(machine.getLevel());
        }
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    private boolean isDimensionBound(int tier, MetaMachine machine) {
        if (machine == null) return true;
        IDimensionTransferData Dimension = DIMENSIONAL_TRANSFER_DATA.get(machine.getLevel());
        if (Dimension != null) {
            if (Dimension.Voltagelevel() >= 14) {
                return false;
            } else {
                return Dimension.Voltagelevel() > tier;
            }
        } else {
            return true;
        }
    }
}
