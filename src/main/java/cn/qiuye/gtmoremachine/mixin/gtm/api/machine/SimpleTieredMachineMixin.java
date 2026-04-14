package cn.qiuye.gtmoremachine.mixin.gtm.api.machine;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.trait.ProgrammableCircuitHandler;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleTieredMachine.class)
public abstract class SimpleTieredMachineMixin extends WorkableTieredMachine {

    @Mutable
    @Final
    @Shadow(remap = false)
    protected NotifiableItemStackHandler circuitInventory;

    private SimpleTieredMachineMixin(BlockEntityCreationInfo holder, int tier, Int2IntFunction tankScalingFunction) {
        super(holder, tier, tankScalingFunction);
    }

    @Inject(
            method = "<init>(Lcom/gregtechceu/gtceu/api/blockentity/BlockEntityCreationInfo;ILit/unimi/dsi/fastutil/ints/Int2IntFunction;)V",
            at = @At("RETURN"),
            remap = false)
    private void replacecircuitInventory(BlockEntityCreationInfo info, int tier, Int2IntFunction tankScalingFunction, CallbackInfo ci) {
        if (GTmm.Mods.isAE2Loaded()) {
            this.circuitInventory = this.attachTrait(new ProgrammableCircuitHandler());
            this.importItems.setFilter(i -> !i.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get()) || !i.hasTag());
        }
    }
}
