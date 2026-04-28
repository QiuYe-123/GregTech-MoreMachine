package cn.qiuye.gtmoremachine.data.recipes.misc

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.common.data.machines.CustomMachines
import cn.qiuye.gtmoremachine.common.data.machines.GTMMAEMachines
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems

import com.gregtechceu.gtceu.api.GTCEuAPI
import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.api.data.tag.TagPrefix
import com.gregtechceu.gtceu.common.data.GTItems
import com.gregtechceu.gtceu.common.data.GTMachines
import com.gregtechceu.gtceu.common.data.GTMaterials
import com.gregtechceu.gtceu.common.data.GTRecipeTypes
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines
import com.gregtechceu.gtceu.data.recipe.CustomTags

import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.ItemStack

import appeng.core.definitions.AEBlocks
import appeng.core.definitions.AEItems

import java.util.Locale

object AE2AssemblerRecipe {

	fun init(provider: RecipeOutput) {
		GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("programmable_cover"))
			.inputItems(GTItems.ROBOT_ARM_LV.asStack(2))
			.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
			.inputItems(CustomTags.MV_CIRCUITS, 2)
			.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
			.outputItems(GTMMAEItems.PROGRAMMABLE_COVER)
			.duration(200)
			.EUt(GTValues.VA[GTValues.LV].toLong())
			.save(provider)

		GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("virtual_item_provider"))
			.inputItems(GTItems.PROGRAMMED_CIRCUIT.asStack())
			.inputItems(ItemStack(AEBlocks.QUARTZ_VIBRANT_GLASS.block().asItem()))
			.inputItems(TagPrefix.foil, GTMaterials.PolyvinylChloride, 8)
			.outputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
			.EUt(480)
			.duration(200)
			.save(provider)

		GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("virtual_item_provider_cell"))
			.inputItems(ItemStack(AEItems.ITEM_CELL_256K.asItem()))
			.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
			.inputItems(GTItems.CONVEYOR_MODULE_HV.asStack(2))
			.inputFluids(GTMaterials.Polyethylene.getFluid(288))
			.outputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER_CELL.asStack())
			.EUt(480)
			.duration(800)
			.save(provider)

		GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("me_export_buffer"))
			.inputItems(GTAEMachines.ITEM_EXPORT_BUS_ME.asStack())
			.inputItems(GTAEMachines.FLUID_EXPORT_HATCH_ME.asStack())
			.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
			.outputItems(GTMMAEMachines.ME_EXPORT_BUFFER.asStack())
			.duration(400)
			.EUt(GTValues.VA[GTValues.HV].toLong())
			.save(provider)

		if (GTCEuAPI.isHighTier()) {
			GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
				GTmm.id(
					"programmablec_hatch_" + GTValues.VN[GTValues.MAX].lowercase(
						Locale.getDefault(),
					) + "_4a",
				),
			)
				.inputItems(GTMachines.DUAL_IMPORT_HATCH[GTValues.MAX].asStack())
				.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
				.inputItems(CustomTags.CIRCUITS_ARRAY[GTValues.MAX], 4)
				.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
				.outputItems(GTMMAEMachines.PROGRAMMABLEC_HATCH[GTValues.MAX].asStack())
				.duration(400)
				.EUt(GTValues.VA[GTValues.MAX].toLong())
				.save(provider)

			GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
				GTmm.id(
					"programmablec_dualhatch_" + GTValues.VN[GTValues.MAX].lowercase(
						Locale.getDefault(),
					) + "_4a",
				),
			)
				.inputItems(CustomMachines.HUGE_INPUT_DUAL_HATCH[GTValues.MAX].asStack())
				.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
				.inputItems(CustomTags.CIRCUITS_ARRAY[GTValues.MAX], 4)
				.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
				.outputItems(GTMMAEMachines.PROGRAMMABLEC_DUALHATCH[GTValues.MAX].asStack())
				.duration(400)
				.EUt(GTValues.VA[GTValues.MAX].toLong())
				.save(provider)
		}
		for (tier in GTValues.tiersBetween(GTValues.LV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
			if (tier > GTValues.IV && GTmm.Mods.isAE2Loaded()) {
				GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
					GTmm.id(
						"programmablec_hatch_" + GTValues.VN[tier].lowercase(
							Locale.getDefault(),
						) + "_4a",
					),
				)
					.inputItems(GTMachines.DUAL_IMPORT_HATCH[tier].asStack())
					.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
					.inputItems(CustomTags.CIRCUITS_ARRAY[tier], 4)
					.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
					.outputItems(GTMMAEMachines.PROGRAMMABLEC_HATCH[tier].asStack())
					.duration(400)
					.EUt(GTValues.VA[tier].toLong())
					.save(provider)

				GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
					GTmm.id(
						"programmablec_dualhatch_" + GTValues.VN[tier].lowercase(
							Locale.getDefault(),
						) + "_4a",
					),
				)
					.inputItems(CustomMachines.HUGE_INPUT_DUAL_HATCH[tier].asStack())
					.inputItems(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack())
					.inputItems(CustomTags.CIRCUITS_ARRAY[tier], 4)
					.inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
					.outputItems(GTMMAEMachines.PROGRAMMABLEC_DUALHATCH[tier].asStack())
					.duration(400)
					.EUt(GTValues.VA[tier].toLong())
					.save(provider)
			}
		}
	}
}
