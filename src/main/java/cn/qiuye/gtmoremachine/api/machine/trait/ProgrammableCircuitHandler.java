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

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import lombok.Setter;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProgrammableCircuitHandler extends NotifiableItemStackHandler {

    public ProgrammableCircuitHandler() {
        super(1, IO.IN, IO.IN, ItemStackHandler::new);
    }

    @Override
    public void setMachine(MetaMachine machine) {
        super.setMachine(machine);
        ((ItemStackHandler) this.storage).setMachine(this.getMachine());
    }

    @Override
    public List<Object> getContents() {
        return Collections.singletonList(this.storage.getStackInSlot(0));
    }

    @Override
    public double getTotalContentAmount() {
        return this.storage.getStackInSlot(0).getCount();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    private static class ItemStackHandler extends CustomItemStackHandler {

        @Setter
        private MetaMachine machine;

        private ItemStackHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get())) {
                boolean allow = true;
                if (this.machine instanceof SimpleTieredMachine tieredMachine) {
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

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return simulate ? super.extractItem(slot, amount, true) : ItemStack.EMPTY;
        }
    }
}
