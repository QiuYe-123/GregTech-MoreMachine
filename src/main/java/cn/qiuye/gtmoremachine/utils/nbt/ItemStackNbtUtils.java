package cn.qiuye.gtmoremachine.utils.nbt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public final class ItemStackNbtUtils {

    private ItemStackNbtUtils() {}

    public static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static boolean hasTag(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA) && !getTag(stack).isEmpty();
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    public static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static void removeKeys(ItemStack stack, String... keys) {
        updateTag(stack, tag -> {
            for (String key : keys) {
                tag.remove(key);
            }
        });
    }
}
