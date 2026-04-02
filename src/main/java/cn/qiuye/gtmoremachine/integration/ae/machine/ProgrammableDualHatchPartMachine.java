package cn.qiuye.gtmoremachine.integration.ae.machine;

import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeDualHatchPartMachine;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

public class ProgrammableDualHatchPartMachine extends HugeDualHatchPartMachine {

    public ProgrammableDualHatchPartMachine(BlockEntityCreationInfo holder, int tier) {
        super(holder, tier, IO.IN);
        this.inventory = new NotifiableItemStackHandler(this, getInventorySize(), IO.IN)
                .setFilter(itemStack -> !itemStack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()));
    }

    @Override
    protected NotifiableItemStackHandler createInventory(IO io) {
        return new NotifiableItemStackHandler(this, getInventorySize(), io) {

            @Override
            public boolean canCapOutput() {
                return true;
            }
        }.setFilter(itemStack -> !itemStack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()));
    }

    @Override
    protected NotifiableItemStackHandler createCircuitItemHandler(IO io) {
        if (io == IO.IN) {
            return new ProgrammableCircuitHandler(this);
        } else {
            return new NotifiableItemStackHandler(this, 0, IO.NONE);
        }
    }
}
