package cn.qiuye.gtmoremachine.api.machine.trait;

import cn.qiuye.gtmoremachine.common.cover.ProgrammableCover;
import cn.qiuye.gtmoremachine.common.item.VirtualItemProviderBehavior;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ProgrammableCircuitHandler extends NotifiableItemStackHandler {

    public ProgrammableCircuitHandler(Object machine) {
        super((MetaMachine) machine, 1, IO.IN, IO.IN, size -> new ItemStackHandler(size, machine));
    }

    @Override
    @NotNull
    public List<Object> getContents() {
        return Collections.singletonList(storage.getStackInSlot(0));
    }

    @Override
    public double getTotalContentAmount() {
        return storage.getStackInSlot(0).getCount();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    private static class ItemStackHandler extends CustomItemStackHandler {

        private final Object machine;

        private ItemStackHandler(int size, Object machine) {
            super(size);
            this.machine = machine;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get())) {
                boolean allow = true;
                if (machine instanceof SimpleTieredMachine tieredMachine) {
                    allow = false;
                    for (CoverBehavior cover : tieredMachine.getCoverContainer().getCovers()) {
                        if (cover instanceof ProgrammableCover) {
                            allow = true;
                            break;
                        }
                    }
                }
                if (allow) {
                    setStackInSlot(slot, VirtualItemProviderBehavior.getVirtualItem(stack));
                    return ItemStack.EMPTY;
                }
            }
            return stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return simulate ? super.extractItem(slot, amount, true) : ItemStack.EMPTY;
        }
    }
}
