package cn.qiuye.gtmoremachine.common.item.datacomponents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record VirtualItemProviderData(ItemStack virtualItem, boolean marked, List<ItemStack> configInventory) {

    public static final VirtualItemProviderData DEFAULT = new VirtualItemProviderData(ItemStack.EMPTY, false, List.of());

    public static final Codec<VirtualItemProviderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("virtual_item", ItemStack.EMPTY).forGetter(VirtualItemProviderData::virtualItem),
            Codec.BOOL.optionalFieldOf("marked", false).forGetter(VirtualItemProviderData::marked),
            ItemStack.CODEC.listOf().optionalFieldOf("config_inventory", List.of()).forGetter(VirtualItemProviderData::configInventory)).apply(instance, VirtualItemProviderData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VirtualItemProviderData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public VirtualItemProviderData {
        virtualItem = virtualItem.copy();
        configInventory = configInventory.stream().map(ItemStack::copy).toList();
    }

    public VirtualItemProviderData withVirtualItem(ItemStack virtualItem) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }

    public VirtualItemProviderData withMarked(boolean marked) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }

    public VirtualItemProviderData withConfigInventory(List<ItemStack> configInventory) {
        return new VirtualItemProviderData(virtualItem, marked, configInventory);
    }
}
