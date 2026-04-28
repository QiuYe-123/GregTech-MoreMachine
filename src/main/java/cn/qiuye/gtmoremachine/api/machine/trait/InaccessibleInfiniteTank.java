package cn.qiuye.gtmoremachine.api.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import appeng.api.stacks.AEFluidKey;

import java.util.Collections;
import java.util.List;

public class InaccessibleInfiniteTank extends NotifiableFluidTank {

    private final FluidStorageDelegate storage;

    public InaccessibleInfiniteTank(MetaMachine machine, KeyStorage internalBuffer) {
        super(machine, List.of(new FluidStorageDelegate(internalBuffer)), IO.OUT, IO.NONE);
        internalBuffer.setOnContentsChanged(this::onContentsChanged);
        storage = (FluidStorageDelegate) getStorages()[0];
        allowSameFluids = true;
    }

    public static Fluid getFirst(SizedFluidIngredient fluidIngredient) {
        for (FluidStack stack : fluidIngredient.getFluids()) {
            if (!stack.isEmpty()) {
                return stack.getFluid();
            }
        }
        return null;
    }

    @Override
    public List<SizedFluidIngredient> handleRecipe(IO io, GTRecipe recipe, List<?> left, boolean simulate) {
        if (!simulate && io == IO.OUT) {
            for (Object ingredient : left) {
                var sizedIngredient = (SizedFluidIngredient) ingredient;
                if (sizedIngredient.ingredient().hasNoFluids()) continue;
                Fluid fluid = getFirst(sizedIngredient);
                if (fluid != null) {
                    storage.fill(sizedIngredient.getFluids()[0], sizedIngredient.amount());
                }
            }
            storage.internalBuffer.onChanged();
            return null;
        }
        return null;
    }

    @Override
    public int getTanks() {
        return 128;
    }

    @Override
    public List<Object> getContents() {
        return Collections.emptyList();
    }

    @Override
    public double getTotalContentAmount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public void setFluidInTank(int tank, FluidStack fluidStack) {}

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public List<SizedFluidIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<SizedFluidIngredient> left, boolean simulate) {
        if (io != IO.OUT) return left;
        FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
        for (var it = left.listIterator(); it.hasNext();) {
            var ingredient = it.next();
            if (ingredient.ingredient().hasNoFluids()) {
                it.remove();
                continue;
            }

            var fluids = ingredient.getFluids();
            if (fluids.length == 0 || fluids[0].isEmpty()) {
                it.remove();
                continue;
            }

            FluidStack output = fluids[0];
            int remaining = ingredient.amount() - storage.fill(output, action);
            if (remaining <= 0) {
                it.remove();
            } else {
                it.set(new SizedFluidIngredient(ingredient.ingredient(), remaining));
            }
        }
        return left.isEmpty() ? null : left;
    }

    private static class FluidStorageDelegate extends CustomFluidTank {

        private final KeyStorage internalBuffer;

        private FluidStorageDelegate(KeyStorage internalBuffer) {
            super(0);
            this.internalBuffer = internalBuffer;
        }

        private void fill(FluidStack fluid, int amount) {
            var key = AEFluidKey.of(fluid);
            long oldValue = internalBuffer.storage.getOrDefault(key, 0);
            long changeValue = Math.min(Long.MAX_VALUE - oldValue, amount);
            if (changeValue > 0) {
                internalBuffer.storage.put(key, oldValue + changeValue);
            }
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void setFluid(FluidStack fluid) {}

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            var key = AEFluidKey.of(resource);
            int amount = resource.getAmount();
            long oldValue = internalBuffer.storage.getOrDefault(key, 0);
            long changeValue = Math.min(Long.MAX_VALUE - oldValue, amount);
            if (changeValue > 0 && action.execute()) {
                internalBuffer.storage.put(key, oldValue + changeValue);
            }
            return (int) changeValue;
        }

        @Override
        public boolean supportsFill(int tank) {
            return false;
        }

        @Override
        public boolean supportsDrain(int tank) {
            return false;
        }
    }
}
