package cn.qiuye.gtmoremachine.integration.ae.machine;

import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;

public class ProgrammableHatchPartMachine extends DualHatchPartMachine {

    public ProgrammableHatchPartMachine(BlockEntityCreationInfo holder, int tier) {
        super(holder, tier, IO.IN);
    }

    @Override
    protected NotifiableItemStackHandler createInventory() {
        return new NotifiableItemStackHandler(getInventorySize(), this.io).setFilter(itemStack -> !itemStack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()));
    }

    @Override
    protected NotifiableItemStackHandler createCircuitItemHandler(IO io) {
        if (io == IO.IN) {
            return new ProgrammableCircuitHandler();
        } else {
            return new NotifiableItemStackHandler(0, IO.NONE);
        }
    }
}
