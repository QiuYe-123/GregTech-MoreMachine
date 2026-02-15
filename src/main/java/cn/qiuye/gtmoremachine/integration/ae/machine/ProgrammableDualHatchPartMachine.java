package cn.qiuye.gtmoremachine.integration.ae.machine;

import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeDualHatchPartMachine;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProgrammableDualHatchPartMachine extends HugeDualHatchPartMachine {

    public ProgrammableDualHatchPartMachine(BlockEntityCreationInfo holder, int tier, IO io) {
        super(holder, tier, io);
    }

    @Override
    protected NotifiableItemStackHandler createInventory(IO io) {
        return new NotifiableItemStackHandler(this, getInventorySize(), io).setFilter(itemStack -> !itemStack.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()));
    }

    @Override
    protected NotifiableItemStackHandler createCircuitItemHandler(Object @NotNull... args) {
        if (args.length > 0 && args[0] instanceof IO io && io == IO.IN) {
            return new ProgrammableCircuitHandler(this);
        } else {
            return new NotifiableItemStackHandler(this, 0, IO.NONE);
        }
    }
}
