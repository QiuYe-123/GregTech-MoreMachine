package cn.qiuye.gtmoremachine.api.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.utils.FluidStackHashStrategy;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CatalystFluidStackHandler extends NotifiableFluidTank {

    public CatalystFluidStackHandler(MetaMachine machine, int slots, int capacity, IO io, IO capabilityIO) {
        super(machine, slots, capacity, io, capabilityIO);
    }

    @Override
    public @Nullable List<SizedFluidIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<SizedFluidIngredient> left, boolean simulate) {
        Object2IntMap<FluidStack> map = new Object2IntOpenCustomHashMap<>(FluidStackHashStrategy.comparingAllButAmount());
        CustomFluidTank[] storages = getStorages();
        for (CustomFluidTank storage : storages) {
            map.putIfAbsent(storage.getFluid(), 1);
        }

        for (Content content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
            var ingredient = (SizedFluidIngredient) content.getContent();
            for (FluidStack is : map.keySet()) {
                if (ingredient.ingredient().test(is) && content.chance > 0) return left;
            }
        }

        return super.handleRecipeInner(io, recipe, left, simulate);
    }
}
