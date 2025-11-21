package cn.qiuye.gtmoremachine.mixin.gtm.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.utils.NumberUtils;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockore.WeightedMaterial;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.BedrockOreMinerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FluidDrillMachine;
import com.gregtechceu.gtceu.common.machine.trait.BedrockOreMinerLogic;
import com.gregtechceu.gtceu.common.machine.trait.FluidDrillLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.material.Fluid;
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
			textList.add(Component.translatable("gtceu.multiblock.max_energy_per_tick", NumberUtils.formatLong(maxVoltage), voltageName));

			if (self.getRecipeLogic().getVeinMaterials() != null) {
				// Ore names
				textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ores_list")
						.withStyle(ChatFormatting.GREEN));
				List<WeightedMaterial> drilledOres = self.getRecipeLogic().getVeinMaterials();
				for (var entry : drilledOres) {
					Component fluidInfo = entry.material().getLocalizedName().withStyle(ChatFormatting.GREEN);
					textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ore_entry", fluidInfo)
							.withStyle(ChatFormatting.GRAY));
				}

				// Ore amount
				Component amountInfo = Component.literal(FormattingUtil.formatNumbers(
						self.getRecipeLogic().getOreToProduce() * 20L / BedrockOreMinerLogic.MAX_PROGRESS) +
						"/s").withStyle(ChatFormatting.BLUE);
				textList.add(Component.translatable("gtceu.multiblock.ore_rig.ore_amount", amountInfo)
						.withStyle(ChatFormatting.GRAY));
			} else {
				Component noOre = Component.translatable("gtceu.multiblock.fluid_rig.no_fluid_in_area")
						.withStyle(ChatFormatting.RED);
				textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ores_list")
						.withStyle(ChatFormatting.GREEN));
				textList.add(Component.translatable("gtceu.multiblock.ore_rig.drilled_ore_entry", noOre)
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
