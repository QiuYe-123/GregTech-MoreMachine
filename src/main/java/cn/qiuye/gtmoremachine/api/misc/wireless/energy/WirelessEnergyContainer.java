package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import cn.qiuye.gtmoremachine.api.capability.wireless.energy.IWirelessLoss;
import cn.qiuye.gtmoremachine.api.misc.time.TimeStat;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature.ICapacitylimitData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature.IDimensionTransferData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature.ITransferData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.record.BasicTransferData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.record.CapacityStorageData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.record.DimensionBoundData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.record.LossEnergy;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.record.StoragePercentageData;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.data.wireless.energy.WirelessEnergySavedData;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.UUID;
import java.util.WeakHashMap;

@Getter
public class WirelessEnergyContainer {

    public static boolean observed;

    public static final WeakHashMap<MetaMachine, ITransferData> TRANSFER_DATA = new WeakHashMap<>();
    public static final WeakHashMap<MetaMachine, IDimensionTransferData> DIMENSIONAL_TRANSFER_DATA = new WeakHashMap<>();
    public static final WeakHashMap<MetaMachine, ICapacitylimitData> CAPACITY_STORAGE_DATA = new WeakHashMap<>();

    private final Object2IntOpenHashMap<ResourceLocation> dimension = new Object2IntOpenHashMap<>();

    public static MinecraftServer server;

    public static WirelessEnergyContainer getOrCreateContainer(UUID uuid) {
        return WirelessEnergySavedData.INSTANCE.containerMap.computeIfAbsent(TeamUtils.getTeamUUID(uuid), WirelessEnergyContainer::new);
    }

    private BigInteger storage;

    private BigInteger rate;

    private GlobalPos bindPos;

    private BigInteger capacity;

    private BigInteger passiveDrain;

    private final UUID UUID;

    private final TimeStat EnergyStat;

    public WirelessEnergyContainer(UUID uuid, BigInteger storage, BigInteger rate, BigInteger capacity, BigInteger passiveDrain, GlobalPos bindPos) {
        this.storage = storage;
        this.rate = rate;
        this.bindPos = bindPos;
        this.capacity = capacity;
        this.passiveDrain = passiveDrain;
        this.UUID = uuid;
        this.EnergyStat = new TimeStat();
    }

    private WirelessEnergyContainer(UUID uuid) {
        this.UUID = uuid;
        this.storage = BigInteger.ZERO;
        this.rate = BigInteger.ZERO;
        this.capacity = BigInteger.ZERO;
        this.passiveDrain = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.EnergyStat = new TimeStat(currentTick);
    }

    public long addTierEnergy(long energy, MetaMachine machine) {
        if (GTMMConfig.INSTANCE.isWirelessDimensionRateEnable && isDimensionBound(machine)) return 0;
        return this.addEnergy(energy, machine);
    }

    public long removeTierEnergy(long energy, MetaMachine machine) {
        if (GTMMConfig.INSTANCE.isWirelessDimensionRateEnable && isDimensionBound(machine)) return 0;
        return this.removeEnergy(energy, machine);
    }

    public long addEnergy(long energy, MetaMachine machine) {
        long change = energy;
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(rate), energy);
        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable && storage.add(BigInteger.valueOf(change)).compareTo(capacity) > 0) change = BigIntegerUtils.getLongValue(capacity.subtract(storage));
        LossEnergy loss = remainingEnergy(change, machine).getAfterEnergy();
        if (loss.getCabinEnergy() <= 0) return 0;
        change = loss.getWirelessEnergy();
        storage = storage.add(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            EnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, new BigInteger(String.valueOf(change)), machine));
        }
        return loss.getCabinEnergy();
    }

    public long removeEnergy(long energy, MetaMachine machine) {
        long change = Math.min(BigIntegerUtils.getLongValue(storage), energy);
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) change = Math.min(BigIntegerUtils.getLongValue(storage), Math.min(BigIntegerUtils.getLongValue(rate), energy));
        LossEnergy loss = remainingEnergy(change, machine);
        if (loss.getCabinEnergy() <= 0) return 0;
        change = loss.getWirelessEnergy();
        storage = storage.subtract(BigInteger.valueOf(change));
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            EnergyStat.update(BigInteger.valueOf(change), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, new BigInteger(String.valueOf(-change)), machine));
        }
        return loss.getCabinEnergy();
    }

    public BigInteger addEnergy(BigInteger energy, MetaMachine machine) {
        BigInteger change = energy;
        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable && storage.add(change).compareTo(capacity) > 0) change = capacity.subtract(storage);
        if (change.compareTo(BigInteger.ZERO) <= 0) return BigInteger.ZERO;
        storage = storage.add(change);
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            EnergyStat.update(change, server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, change, machine));
        }
        return change;
    }

    public BigInteger removeEnergy(BigInteger energy, MetaMachine machine) {
        BigInteger change = storage.min(energy);
        if (change.compareTo(BigInteger.ZERO) <= 0) return BigInteger.ZERO;
        storage = storage.subtract(change);
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        if (machine != null) {
            EnergyStat.update(change, server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, change.negate(), machine));
        }
        return change;
    }

    public void PassiveDrainEnergy(BigInteger energy) {
        BigInteger change = storage.min(energy);
        if (change.compareTo(BigInteger.ZERO) <= 0) return;
        storage = storage.subtract(change);
        WirelessEnergySavedData.INSTANCE.setDirty(true);
        EnergyStat.update(change.negate(), server.getTickCount());
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
            if (machine != null) CAPACITY_STORAGE_DATA.put(machine, new CapacityStorageData(UUID, StorageCapacity, PassiveDrain, machine));
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
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    public void setDimensional(int Voltagelevel, boolean Bind, MetaMachine machine) {
        ResourceLocation tierdimension = machine.getLevel().dimension().location();
        if (Bind) {
            if (this.dimension.getInt(tierdimension) <= Voltagelevel || !this.dimension.containsKey(tierdimension)) {
                DIMENSIONAL_TRANSFER_DATA.put(machine, new DimensionBoundData(UUID, Voltagelevel, machine));
                this.dimension.put(tierdimension, Voltagelevel);
            }
        } else {
            if (this.dimension.getInt(tierdimension) > Voltagelevel) {
                DIMENSIONAL_TRANSFER_DATA.remove(machine);
                this.dimension.removeInt(tierdimension);
            }
        }
        WirelessEnergySavedData.INSTANCE.setDirty(true);
    }

    private boolean isDimensionBound(MetaMachine machine) {
        if (machine == null) return true;
        if (machine instanceof ITieredMachine velataeTier) {
            Level level = machine.getLevel();
            if (level == null) return true;
            return this.dimension.getInt(machine.getLevel().dimension().location()) < velataeTier.getTier();
        }
        return true;
    }

    private LossEnergy remainingEnergy(long energy, MetaMachine machine) {
        if (!(machine instanceof IWirelessLoss lossMachine)) {
            return new LossEnergy(energy, energy);
        }
        long loss = lossMachine.LossNumber();
        long afterLoss = switch (lossMachine.LossType()) {
            case Fixed -> Math.max(energy - loss, 0);
            case Percentage -> Math.max(Math.round(energy * (1 - loss / 100.0)), 0);
            case None -> energy;
        };
        return new LossEnergy(afterLoss, energy);
    }
}
