package cn.qiuye.gtmoremachine.common.item.itemstack;

import cn.qiuye.gtmoremachine.common.data.GTMMDataComponents;
import cn.qiuye.gtmoremachine.common.item.VirtualItemProviderBehavior;
import cn.qiuye.gtmoremachine.common.item.datacomponents.VirtualItemProviderData;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.stacks.GenericStack;
import appeng.items.storage.CreativeCellItem;
import appeng.util.ConfigInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class VirtualItemProviderCellItem extends CreativeCellItem {

    public VirtualItemProviderCellItem(Properties props) {
        super(props);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        Holder holder = new Holder(is);
        holder.inv = new VirtualConfigInventory(63, holder::save);
        holder.load();
        return holder.inv;
    }

    private static class Holder {

        private final ItemStack stack;
        private ConfigInventory inv;

        Holder(ItemStack stack) {
            this.stack = stack;
        }

        void load() {
            var data = VirtualItemProviderBehavior.getVirtualItemProviderData(stack);
            var configuredStacks = data.configInventory();
            int slotCount = Math.min(inv.size(), configuredStacks.size());
            for (int slot = 0; slot < slotCount; slot++) {
                ItemStack configuredStack = configuredStacks.get(slot);
                if (configuredStack.isEmpty()) {
                    continue;
                }
                AEItemKey itemKey = AEItemKey.of(configuredStack);
                if (itemKey != null) {
                    inv.setStack(slot, new GenericStack(itemKey, 0L));
                }
            }
        }

        void save() {
            var data = VirtualItemProviderBehavior.getVirtualItemProviderData(stack);
            List<ItemStack> configuredStacks = new ArrayList<>(inv.size());
            for (int slot = 0; slot < inv.size(); slot++) {
                GenericStack genericStack = inv.getStack(slot);
                if (genericStack != null && genericStack.what() instanceof AEItemKey itemKey) {
                    configuredStacks.add(itemKey.toStack());
                } else {
                    configuredStacks.add(ItemStack.EMPTY);
                }
            }
            VirtualItemProviderBehavior.setVirtualItemProviderData(stack, data.withConfigInventory(configuredStacks));
        }
    }

    private static class VirtualConfigInventory extends ConfigInventory {

        private VirtualConfigInventory(int size, @Nullable Runnable listener) {
            super(AEKeyTypes.getAll(), null, Mode.CONFIG_TYPES, size, listener, false);
        }

        public void setStack(int slot, @Nullable GenericStack stack) {
            if (stack == null) {
                super.setStack(slot, null);
            } else if (stack.what() instanceof AEItemKey itemKey &&
                    itemKey.getItem() == GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asItem() &&
                    VirtualItemProviderBehavior.hasVirtualItem(itemKey.getReadOnlyStack())) {
                        boolean typesOnly = this.mode == Mode.CONFIG_TYPES;
                        ItemStack markedStack = itemKey.toStack();
                        var data = markedStack.getOrDefault(GTMMDataComponents.VIRTUAL_ITEM_PROVIDER.get(), VirtualItemProviderData.DEFAULT);
                        VirtualItemProviderBehavior.setVirtualItemProviderData(markedStack, data.withMarked(true));
                        itemKey = AEItemKey.of(markedStack);
                        if (itemKey == null) return;
                        if (typesOnly && stack.amount() != 0L) {
                            stack = new GenericStack(itemKey, 0L);
                        } else if (!typesOnly && stack.amount() <= 0L) {
                            stack = null;
                        }

                        super.setStack(slot, stack);
                    }
        }
    }
}
