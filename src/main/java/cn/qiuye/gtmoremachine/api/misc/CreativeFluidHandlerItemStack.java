package cn.qiuye.gtmoremachine.api.misc;

import com.gregtechceu.gtceu.api.capability.IThermalFluidHandlerItemStack;
import com.gregtechceu.gtceu.common.data.item.GTDataComponents;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import org.jetbrains.annotations.NotNull;

public class CreativeFluidHandlerItemStack extends FluidHandlerItemStack implements IThermalFluidHandlerItemStack {

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public CreativeFluidHandlerItemStack(@NotNull ItemStack container, int capacity, FluidStack fluidStack) {
        super(GTDataComponents.FLUID_CONTENT, container, capacity);
        this.setFluid(fluidStack);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isFluidEqual(this.getFluid())) {
            if (capacity == Integer.MAX_VALUE) {
                return resource.copy();
            } else {
                FluidStack fluidStack = resource.copy();
                fluidStack.setAmount(Math.min(resource.getAmount(), capacity));
                return fluidStack;
            }
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public int fill(FluidStack resource, FluidAction doFill) {
        return resource.getAmount();
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack contained = this.getFluid();
        if (contained.isEmpty()) return FluidStack.EMPTY;
        FluidStack drained = contained.copy();
        drained.setAmount(Math.min(maxDrain, capacity));
        return drained;
    }

    @Override
    public int getMaxFluidTemperature() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isGasProof() {
        return true;
    }

    @Override
    public boolean isAcidProof() {
        return true;
    }

    @Override
    public boolean isCryoProof() {
        return true;
    }

    @Override
    public boolean isPlasmaProof() {
        return true;
    }
}
