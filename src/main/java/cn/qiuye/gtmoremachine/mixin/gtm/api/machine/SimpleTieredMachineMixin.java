package cn.qiuye.gtmoremachine.mixin.gtm.api.machine;

import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleTieredMachine.class)
public class SimpleTieredMachineMixin extends WorkableTieredMachine {

    private SimpleTieredMachineMixin(BlockEntityCreationInfo holder, int tier, Int2IntFunction tankScalingFunction) {
        super(holder, tier, tankScalingFunction);
    }

    @Redirect(
            method = "<init>(Lcom/gregtechceu/gtceu/api/blockentity/BlockEntityCreationInfo;ILit/unimi/dsi/fastutil/ints/Int2IntFunction;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/gregtechceu/gtceu/api/machine/trait/NotifiableItemStackHandler;<init>(Lcom/gregtechceu/gtceu/api/machine/MetaMachine;ILcom/gregtechceu/gtceu/api/capability/recipe/IO;Lcom/gregtechceu/gtceu/api/capability/recipe/IO;)V"
            ),
            remap = false
    )
    private NotifiableItemStackHandler redirectCircuitInventory(MetaMachine machine, int size, IO ioIn, IO ioOut) {
        return new ProgrammableCircuitHandler((SimpleTieredMachine) (Object) this);
    }

    @Override
    protected @NotNull NotifiableItemStackHandler createImportItemHandler(Object @NotNull... args) {
        return new NotifiableItemStackHandler(this, getRecipeType().getMaxInputs(ItemRecipeCapability.CAP), IO.IN).setFilter(i -> !i.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()) || !i.hasTag());
    }
}
