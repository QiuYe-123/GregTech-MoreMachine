package cn.qiuye.gtmoremachine.data.wireless.cwu;

import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

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
import java.util.Objects;
import java.util.UUID;

public class WirelessCWUSavaedData extends SavedData {

    public static WirelessCWUSavaedData INSTANCE;

    public static WirelessCWUSavaedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(WirelessCWUSavaedData::new, WirelessCWUSavaedData::new, "gtceu_wireless_cwu");
    }

    public final Map<UUID, WirelessCWUContainer> containerMap = new HashMap<>();

    public WirelessCWUSavaedData() {}

    public WirelessCWUSavaedData(CompoundTag tag) {
        ListTag allCWU = tag.getList("allCWU", Tag.TAG_COMPOUND);
        for (int i = 0; i < allCWU.size(); i++) {
            WirelessCWUContainer container = readTag(allCWU.getCompound(i));
            containerMap.put(container.getUuid(), container);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag allcwu = new ListTag();
        for (WirelessCWUContainer container : containerMap.values()) {
            CompoundTag engTag = toTag(container);
            if (engTag.isEmpty()) continue;
            allcwu.add(engTag);
        }
        compoundTag.put("allCWU", allcwu);
        return compoundTag;
    }

    protected WirelessCWUContainer readTag(CompoundTag engTag) {
        UUID uuid = engTag.getUUID("uuid");
        String en = engTag.getString("cwu");
        BigInteger cwu = new BigInteger(en.isEmpty() ? "0" : en);
        GlobalPos bindPos = readGlobalPos(engTag.getString("dimension"), engTag.getLong("pos"));
        return new WirelessCWUContainer(uuid, cwu, bindPos);
    }

    protected CompoundTag toTag(WirelessCWUContainer container) {
        CompoundTag engTag = new CompoundTag();
        BigInteger storage = container.getStorage();
        if (!Objects.equals(storage, BigInteger.ZERO)) {
            engTag.putString("cwu", storage.toString());
        }
        GlobalPos bindPos = container.getBindPos();
        if (bindPos != null) {
            engTag.putString("dimension", bindPos.dimension().location().toString());
            engTag.putLong("pos", bindPos.pos().asLong());
        }
        if (!engTag.isEmpty()) engTag.putUUID("uuid", container.getUuid());
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
