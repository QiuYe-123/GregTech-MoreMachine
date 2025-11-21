package cn.qiuye.gtmoremachine.mixin.gtm.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.common.machine.multiblock.electric.BedrockOreMinerMachine;

import net.minecraft.network.chat.Component;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BedrockOreMinerMachine.class)
public class BedrockOreMinerMachineMixin {

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

        BedrockOreMinerMachine self = (BedrockOreMinerMachine) (Object) this;

        if (self.isFormed()) {
            String formattedVoltage = NumberUtils.formatLong(maxVoltage);
            textList.add(Component.translatable("gtceu.multiblock.max_energy_per_tick", formattedVoltage, voltageName));

            if (self.getRecipeLogic().getVeinMaterials() != null) {
                // Ore names
                textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ores_list")
                        .withStyle(net.minecraft.ChatFormatting.GREEN));
                java.util.List<com.gregtechceu.gtceu.api.data.worldgen.bedrockore.WeightedMaterial> drilledOres = self.getRecipeLogic().getVeinMaterials();
                for (var entry : drilledOres) {
                    Component fluidInfo = entry.material().getLocalizedName().withStyle(net.minecraft.ChatFormatting.GREEN);
                    textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ore_entry", fluidInfo)
                            .withStyle(net.minecraft.ChatFormatting.GRAY));
                }

                // Ore amount
                Component amountInfo = Component.literal(com.gregtechceu.gtceu.utils.FormattingUtil.formatNumbers(
                        self.getRecipeLogic().getOreToProduce() * 20L / com.gregtechceu.gtceu.common.machine.trait.BedrockOreMinerLogic.MAX_PROGRESS) +
                        "/s").withStyle(net.minecraft.ChatFormatting.BLUE);
                textList.add(Component.translatable("gtceu.multiblock.ore_rig.ore_amount", amountInfo)
                        .withStyle(net.minecraft.ChatFormatting.GRAY));
            } else {
                Component noOre = Component.translatable("gtceu.multiblock.fluid_rig.no_fluid_in_area")
                        .withStyle(net.minecraft.ChatFormatting.RED);
                textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ores_list")
                        .withStyle(net.minecraft.ChatFormatting.GREEN));
                textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ore_entry", noOre)
                        .withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        } else {
            Component tooltip = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                    .withStyle(net.minecraft.ChatFormatting.GRAY);
            textList.add(Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.RED)
                            .withHoverEvent(new net.minecraft.network.chat.HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, tooltip))));
        }
    }
}
