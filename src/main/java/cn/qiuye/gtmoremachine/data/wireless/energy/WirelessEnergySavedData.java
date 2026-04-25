package cn.qiuye.gtmoremachine.data.wireless.energy;

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WirelessEnergySavedData extends SavedData {

    public static WirelessEnergySavedData INSTANCE;

    public static WirelessEnergySavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(WirelessEnergySavedData::new, WirelessEnergySavedData::new, "gtceu_wireless_energy");
    }

    public final Map<UUID, WirelessEnergyContainer> containerMap = new HashMap<>();

    public WirelessEnergySavedData() {}

    public WirelessEnergySavedData(CompoundTag tag) {
        ListTag allEnergy = tag.getList("allEnergy", Tag.TAG_COMPOUND);
        for (int i = 0; i < allEnergy.size(); i++) {
            WirelessEnergyContainer container = readTag(allEnergy.getCompound(i));
            containerMap.put(container.getUUID(), container);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag allEnergy = new ListTag();
        for (WirelessEnergyContainer container : containerMap.values()) {
            CompoundTag engTag = toTag(container);
            if (engTag.isEmpty()) continue;
            allEnergy.add(engTag);
        }
        compoundTag.put("allEnergy", allEnergy);
        return compoundTag;
    }

    protected WirelessEnergyContainer readTag(CompoundTag engTag) {
        UUID uuid = engTag.getUUID("uuid");
        String en = engTag.getString("energy");
        String ra = engTag.getString("rate");
        String ca = engTag.getString("capacity");
        String pd = engTag.getString("passiveDrain");
        BigInteger energy = en.isEmpty() ? BigInteger.ZERO : new BigInteger(en, 16);
        BigInteger rate = ra.isEmpty() ? BigInteger.ZERO : new BigInteger(ra, 16);
        BigInteger capacity = ca.isEmpty() ? BigInteger.ZERO : new BigInteger(ca, 16);
        BigInteger passiveDrain = pd.isEmpty() ? BigInteger.ZERO : new BigInteger(pd, 16);
        GlobalPos bindPos = readGlobalPos(engTag.getString("dimension"), engTag.getLong("pos"));
        return new WirelessEnergyContainer(uuid, energy, rate, capacity, passiveDrain, bindPos);
    }

    protected CompoundTag toTag(WirelessEnergyContainer container) {
        CompoundTag engTag = new CompoundTag();
        BigInteger storage = container.getStorage();
        if (storage.compareTo(BigInteger.ZERO) > 0) {
            engTag.putString("energy", storage.toString(16));
        }
        BigInteger rate = container.getRate();
        if (rate.compareTo(BigInteger.ZERO) > 0) {
            engTag.putString("rate", rate.toString(16));
        }
        BigInteger capacity = container.getCapacity();
        if (capacity.compareTo(BigInteger.ZERO) > 0) {
            engTag.putString("capacity", capacity.toString(16));
        }
        BigInteger passiveDrain = container.getPassiveDrain();
        if (passiveDrain.compareTo(BigInteger.ZERO) > 0) {
            engTag.putString("passiveDrain", passiveDrain.toString(16));
        }
        GlobalPos bindPos = container.getBindPos();
        if (bindPos != null) {
            engTag.putString("dimension", bindPos.dimension().location().toString());
            engTag.putLong("pos", bindPos.pos().asLong());
        }
        if (!engTag.isEmpty()) engTag.putUUID("uuid", container.getUUID());
        return engTag;
    }

    private static GlobalPos readGlobalPos(String dimension, long pos) {
        if (dimension.isEmpty()) return null;
        if (pos == 0) return null;
        ResourceLocation key = ResourceLocation.tryParse(dimension);
        if (key == null) return null;
        return GlobalPos.of(ResourceKey.create(Registries.DIMENSION, key), BlockPos.of(pos));
    }
}
