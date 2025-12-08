package cn.qiuye.gtmoremachine.data.wireless.cwu;

import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WirelessCWUSavedData extends SavedData {

    public static WirelessCWUSavedData INSTANCE;

    public static WirelessCWUSavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(WirelessCWUSavedData::new, WirelessCWUSavedData::new, "gtceu_wireless_cwu");
    }

    public final Map<UUID, WirelessCWUContainer> containerMap = new HashMap<>();

    public WirelessCWUSavedData() {}

    public WirelessCWUSavedData(CompoundTag tag) {
        ListTag allCWU = tag.getList("allCWU", Tag.TAG_COMPOUND);
        for (int i = 0; i < allCWU.size(); i++) {
            WirelessCWUContainer container = readTag(allCWU.getCompound(i));
            containerMap.put(container.getUUID(), container);
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
        return new WirelessCWUContainer(uuid, cwu);
    }

    protected CompoundTag toTag(WirelessCWUContainer container) {
        CompoundTag engTag = new CompoundTag();
        BigInteger storage = container.getStorage();
        if (!Objects.equals(storage, BigInteger.ZERO)) {
            engTag.putString("cwu", storage.toString());
        }
        if (!engTag.isEmpty()) engTag.putUUID("uuid", container.getUUID());
        return engTag;
    }
}
