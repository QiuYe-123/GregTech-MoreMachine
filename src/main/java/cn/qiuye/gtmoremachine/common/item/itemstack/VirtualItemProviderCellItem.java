package cn.qiuye.gtmoremachine.common.item.itemstack;

import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;
import cn.qiuye.gtmoremachine.utils.nbt.ItemStackNbtUtils;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.stacks.GenericStack;
import appeng.items.storage.CreativeCellItem;
import appeng.util.ConfigInventory;
import org.jetbrains.annotations.Nullable;

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
            if (ItemStackNbtUtils.hasTag(stack)) {
                inv.readFromChildTag(ItemStackNbtUtils.getTag(stack), "list", GTRegistries.builtinRegistry());
            }
        }

        void save() {
            ItemStackNbtUtils.updateTag(stack, tag -> inv.writeToChildTag(tag, "list", GTRegistries.builtinRegistry()));
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
                    ItemStackNbtUtils.hasTag(itemKey.getReadOnlyStack())) {
                        boolean typesOnly = this.mode == Mode.CONFIG_TYPES;
                        ItemStack markedStack = itemKey.toStack();
                        ItemStackNbtUtils.updateTag(markedStack, tag -> tag.putBoolean("marked", true));
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
