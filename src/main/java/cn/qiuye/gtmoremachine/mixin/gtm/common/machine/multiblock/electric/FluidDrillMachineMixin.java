package cn.qiuye.gtmoremachine.mixin.gtm.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.common.machine.multiblock.electric.FluidDrillMachine;
import com.gregtechceu.gtceu.common.machine.trait.FluidDrillLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.material.Fluid;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FluidDrillMachine.class)
public class FluidDrillMachineMixin {

    @Inject(
            method = "addDisplayText",
            at = @At(
                     value = "INVOKE",
                     target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                     ordinal = 0),
            cancellable = true,
            remap = false)
    private void modifyVoltageDisplay(List<Component> textList, CallbackInfo ci,
                                      @Local(name = "maxVoltage") long maxVoltage, @Local(name = "voltageName") String voltageName) {
        ci.cancel();

        FluidDrillMachine self = (FluidDrillMachine) (Object) this;

        if (self.isFormed()) {
            String formattedVoltage = NumberUtils.formatLong(maxVoltage);
            textList.add(Component.translatable("gtceu.multiblock.max_energy_per_tick", formattedVoltage, voltageName));

            if (self.getRecipeLogic().getVeinFluid() != null) {
                // Fluid name
                Fluid drilledFluid = self.getRecipeLogic().getVeinFluid();
                Component fluidInfo = drilledFluid.getFluidType().getDescription().copy()
                        .withStyle(ChatFormatting.GREEN);
                textList.add(Component.translatable("gtceu.multiblock.fluid_rig.drilled_fluid", fluidInfo)
                        .withStyle(ChatFormatting.GRAY));

                // Fluid amount
                Component amountInfo = Component.literal(FormattingUtil.formatNumbers(
                        self.getRecipeLogic().getFluidToProduce() * 20L / FluidDrillLogic.MAX_PROGRESS) +
                        " mB/s").withStyle(ChatFormatting.BLUE);
                textList.add(Component.translatable("gtceu.multiblock.fluid_rig.fluid_amount", amountInfo)
                        .withStyle(ChatFormatting.GRAY));
            } else {
                Component noFluid = Component.translatable("gtceu.multiblock.fluid_rig.no_fluid_in_area")
                        .withStyle(ChatFormatting.RED);
                textList.add(Component.translatable("gtceu.multiblock.fluid_rig.drilled_fluid", noFluid)
                        .withStyle(ChatFormatting.GRAY));
            }
        } else {
            Component tooltip = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                    .withStyle(ChatFormatting.GRAY);
            textList.add(Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip))));
        }
    }
}
