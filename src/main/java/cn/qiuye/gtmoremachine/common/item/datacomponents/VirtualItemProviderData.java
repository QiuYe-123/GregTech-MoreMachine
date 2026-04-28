package cn.qiuye.gtmoremachine.common.item.datacomponents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record VirtualItemProviderData(ItemStack virtualItem, boolean marked, CompoundTag configInventory) {

    public static final VirtualItemProviderData DEFAULT = new VirtualItemProviderData(ItemStack.EMPTY, false, new CompoundTag());

    public static final Codec<VirtualItemProviderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("virtual_item", ItemStack.EMPTY).forGetter(VirtualItemProviderData::virtualItem),
            Codec.BOOL.optionalFieldOf("marked", false).forGetter(VirtualItemProviderData::marked),
            CompoundTag.CODEC.optionalFieldOf("config_inventory", new CompoundTag()).forGetter(VirtualItemProviderData::configInventory)
    ).apply(instance, VirtualItemProviderData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VirtualItemProviderData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public VirtualItemProviderData {
        virtualItem = virtualItem.copy();
        configInventory = configInventory.copy();
    }

    public VirtualItemProviderData withVirtualItem(ItemStack virtualItem) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }

    public VirtualItemProviderData withMarked(boolean marked) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }

    public VirtualItemProviderData withConfigInventory(CompoundTag configInventory) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }
}
