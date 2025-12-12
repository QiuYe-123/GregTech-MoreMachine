package cn.qiuye.gtmoremachine.integration.ae.machine;

import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeDualHatchPartMachine;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import org.jetbrains.annotations.NotNull;

public class ProgrammableDualHatchPartMachine extends HugeDualHatchPartMachine {

    public ProgrammableDualHatchPartMachine(IMachineBlockEntity holder, int tier, IO io, Object... args) {
        super(holder, tier, io, args);
    }

    protected @NotNull NotifiableItemStackHandler createInventory(Object @NotNull... args) {
        return new NotifiableItemStackHandler(this, getInventorySize(), io).setFilter(itemStack -> !itemStack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()));
    }

    @Override
    protected @NotNull NotifiableItemStackHandler createCircuitItemHandler(Object @NotNull... args) {
        if (args.length > 0 && args[0] instanceof IO io && io == IO.IN) {
            return new ProgrammableCircuitHandler(this);
        } else {
            return new NotifiableItemStackHandler(this, 0, IO.NONE);
        }
    }
}
