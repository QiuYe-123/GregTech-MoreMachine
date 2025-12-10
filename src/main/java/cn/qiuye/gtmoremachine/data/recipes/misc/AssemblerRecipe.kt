package cn.qiuye.gtmoremachine.data.recipes.misc

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.common.data.GTMMItems
import cn.qiuye.gtmoremachine.common.data.machines.CustomMachines
import cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines

import com.gregtechceu.gtceu.api.GTCEuAPI
import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.api.data.tag.TagPrefix
import com.gregtechceu.gtceu.api.item.ComponentItem
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType
import com.gregtechceu.gtceu.common.data.*
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines
import com.gregtechceu.gtceu.data.recipe.CustomTags

import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.world.item.Items

import com.tterrag.registrate.util.entry.ItemEntry

import java.util.*
import java.util.function.Consumer

object AssemblerRecipe {

    fun init(provider: Consumer<FinishedRecipe>) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_item_transfer_cover"))
            .inputItems(GTItems.SENSOR_LV.asStack())
            .inputItems(GTItems.EMITTER_LV.asStack())
            .inputItems(GTItems.ROBOT_ARM_LV.asStack())
            .inputItems(CustomTags.LV_CIRCUITS, 2)
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 2)
            .inputItems(TagPrefix.plateDouble, GTMaterials.Steel, 2)
            .inputFluids(GTMaterials.Polyethylene.getFluid(288))
            .outputItems(GTMMItems.WIRELESS_ITEM_TRANSFER_COVER.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_fluid_transfer_cover"))
            .inputItems(GTItems.SENSOR_LV.asStack())
            .inputItems(GTItems.EMITTER_LV.asStack())
            .inputItems(GTItems.FLUID_REGULATOR_LV.asStack())
            .inputItems(CustomTags.LV_CIRCUITS, 2)
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 2)
            .inputItems(TagPrefix.plateDouble, GTMaterials.Steel, 2)
            .inputFluids(GTMaterials.Polyethylene.getFluid(288))
            .outputItems(GTMMItems.WIRELESS_FLUID_TRANSFER_COVER.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("advanced_wireless_item_transfer_cover"))
            .inputItems(GTMMItems.WIRELESS_ITEM_TRANSFER_COVER.asStack())
            .inputItems(CustomTags.MV_CIRCUITS)
            .outputItems(GTMMItems.ADVANCED_WIRELESS_ITEM_TRANSFER_COVER)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(72))
            .duration(100)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("advanced_wireless_fluid_transfer_cover"))
            .inputItems(GTMMItems.WIRELESS_FLUID_TRANSFER_COVER.asStack())
            .inputItems(CustomTags.MV_CIRCUITS)
            .outputItems(GTMMItems.ADVANCED_WIRELESS_FLUID_TRANSFER_COVER)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(72))
            .duration(100)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("cover_maintenance_detector"))
            .inputItems(GTItems.EMITTER_LV)
            .inputItems(TagPrefix.plate, GTMaterials.Steel)
            .circuitMeta(1)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(72))
            .outputItems(GTItems.COVER_MAINTENANCE_DETECTOR)
            .EUt(16)
            .duration(100)
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_monitor"))
            .inputItems(GTMachines.HULL[1].asStack())
            .inputItems(GTItems.COVER_SCREEN)
            .inputItems(Items.ENDER_PEARL, 16)
            .inputItems(GTItems.TERMINAL)
            .inputItems(CustomTags.LV_CIRCUITS, 4)
            .inputItems(TagPrefix.foil, GTMaterials.Steel, 16)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(WirelessMachines.WIRELESS_ENERGY_MONITOR.asStack())
            .duration(400)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_terminal"))
            .inputItems(WirelessMachines.WIRELESS_ENERGY_MONITOR.asStack())
            .inputItems(GTMMItems.WIRELESS_ENERGY_BINDING_TOOL)
            .inputItems(GTItems.COVER_SCREEN)
            .inputItems(Items.ENDER_PEARL, 16)
            .inputItems(GTItems.TERMINAL)
            .inputItems(CustomTags.LV_CIRCUITS, 4)
            .inputItems(TagPrefix.foil, GTMaterials.Steel, 16)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_TERMINAL)
            .duration(400)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_interface"))
            .inputItems(GTMachines.ENERGY_INPUT_HATCH[1].asStack())
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV.asStack())
            .inputItems(Items.ENDER_PEARL, 16)
            .inputItems(CustomTags.LV_CIRCUITS, 4)
            .inputItems(TagPrefix.spring, GTMaterials.Iron, 4)
            .inputItems(TagPrefix.foil, GTMaterials.Steel, 16)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
            .duration(400)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_lv"))
            .inputItems(GTItems.SENSOR_LV.asStack())
            .inputItems(GTItems.EMITTER_LV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.LV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_LV.asStack())
            .inputItems(TagPrefix.spring, GTMaterials.Tin, 1)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.Tin, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.Steel, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_lv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV.asStack(2))
            .inputItems(GTItems.INDUCTOR.asStack(4))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.Tin, 4)
            .inputItems(GTItems.VOLTAGE_COIL_LV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_mv"))
            .inputItems(GTItems.SENSOR_MV.asStack())
            .inputItems(GTItems.EMITTER_MV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.MV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_MV.asStack())
            .inputItems(GTItems.ULTRA_LOW_POWER_INTEGRATED_CIRCUIT.asStack())
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.Copper, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_MV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.MV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_mv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_MV.asStack(2))
            .inputItems(GTItems.INDUCTOR.asStack(8))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.Copper, 4)
            .inputItems(GTItems.VOLTAGE_COIL_MV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_MV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.MV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_hv"))
            .inputItems(GTItems.SENSOR_HV.asStack())
            .inputItems(GTItems.EMITTER_HV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.HV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_HV.asStack())
            .inputItems(GTItems.LOW_POWER_INTEGRATED_CIRCUIT.asStack())
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.Gold, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_HV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.HV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_hv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_HV.asStack(2))
            .inputItems(GTItems.SMD_INDUCTOR.asStack(4))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.Gold, 4)
            .inputItems(GTItems.VOLTAGE_COIL_HV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_HV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.HV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_ev"))
            .inputItems(GTItems.SENSOR_EV.asStack())
            .inputItems(GTItems.EMITTER_EV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.EV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_EV.asStack())
            .inputItems(GTItems.POWER_INTEGRATED_CIRCUIT.asStack())
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.Aluminium, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.Titanium, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_EV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.EV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_ev_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_EV.asStack(2))
            .inputItems(GTItems.SMD_INDUCTOR.asStack(8))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.Aluminium, 4)
            .inputItems(GTItems.VOLTAGE_COIL_EV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_EV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.EV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_iv"))
            .inputItems(GTItems.SENSOR_IV.asStack())
            .inputItems(GTItems.EMITTER_IV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.IV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_IV.asStack())
            .inputItems(GTItems.HIGH_POWER_INTEGRATED_CIRCUIT.asStack())
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.Platinum, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.TungstenSteel, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_IV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.IV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_iv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_IV.asStack(2))
            .inputItems(GTItems.ADVANCED_SMD_INDUCTOR.asStack(4))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.Platinum, 4)
            .inputItems(GTItems.VOLTAGE_COIL_IV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_IV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.IV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_luv"))
            .inputItems(GTItems.SENSOR_LuV.asStack())
            .inputItems(GTItems.EMITTER_LuV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.LuV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_LuV.asStack())
            .inputItems(GTItems.HIGH_POWER_INTEGRATED_CIRCUIT.asStack(2))
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.NiobiumTitanium, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.RhodiumPlatedPalladium, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LuV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_luv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV.asStack(2))
            .inputItems(GTItems.ADVANCED_SMD_INDUCTOR.asStack(8))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.NiobiumTitanium, 4)
            .inputItems(GTItems.VOLTAGE_COIL_LuV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.LuV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_zpm"))
            .inputItems(GTItems.SENSOR_ZPM.asStack())
            .inputItems(GTItems.EMITTER_ZPM.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.ZPM_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_ZPM.asStack())
            .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT.asStack())
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.VanadiumGallium, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.NaquadahAlloy, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.ZPM].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_zpm_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM.asStack(2))
            .inputItems(GTItems.ADVANCED_SMD_INDUCTOR.asStack(16))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.VanadiumGallium, 4)
            .inputItems(GTItems.VOLTAGE_COIL_ZPM.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.ZPM].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_uv"))
            .inputItems(GTItems.SENSOR_UV.asStack())
            .inputItems(GTItems.EMITTER_UV.asStack())
            .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, 4)
            .inputItems(CustomTags.UV_CIRCUITS, 2)
            .inputItems(GTItems.VOLTAGE_COIL_UV.asStack())
            .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT.asStack(2))
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.YttriumBariumCuprate, 2)
            .inputItems(TagPrefix.cableGtSingle, GTMaterials.RedAlloy, 2)
            .inputItems(TagPrefix.plate, GTMaterials.Darmstadtium, 4)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UV.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.UV].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_energy_receive_cover_uv_4a"))
            .inputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UV.asStack(2))
            .inputItems(GTItems.ADVANCED_SMD_INDUCTOR.asStack(32))
            .inputItems(TagPrefix.cableGtQuadruple, GTMaterials.YttriumBariumCuprate, 4)
            .inputItems(GTItems.VOLTAGE_COIL_UV.asStack(2))
            .inputItems(TagPrefix.plateDouble, GTMaterials.BatteryAlloy, 2)
            .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
            .outputItems(GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UV_4A.asStack())
            .duration(200)
            .EUt(GTValues.VA[GTValues.UV].toLong())
            .save(provider)

        val wirelessEnergyReceiveCovers: ArrayList<ItemEntry<ComponentItem>> =
            ArrayList<ItemEntry<ComponentItem>>(
                listOf<ItemEntry<ComponentItem>>(
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_MV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_HV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_EV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_IV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UV,
                ),
            )
        if (GTCEuAPI.isHighTier()) {
            wirelessEnergyReceiveCovers.addAll(
                listOf<ItemEntry<ComponentItem>>(
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UHV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UEV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UIV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UXV,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_OPV,
                ),
            )
        }

        val wirelessEnergyReceiveCover4A: MutableList<ItemEntry<ComponentItem>> =
            ArrayList<ItemEntry<ComponentItem>>(
                listOf<ItemEntry<ComponentItem>>(
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_MV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_HV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_EV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_IV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UV_4A,
                ),
            )
        if (GTCEuAPI.isHighTier()) {
            wirelessEnergyReceiveCover4A.addAll(
                listOf<ItemEntry<ComponentItem>>(
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UHV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UEV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UIV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_UXV_4A,
                    GTMMItems.WIRELESS_ENERGY_RECEIVE_COVER_OPV_4A,
                ),
            )
        }

        for (tier in GTValues.tiersBetween(GTValues.LV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ),
                ),
            )
                .inputItems(GTMachines.ENERGY_INPUT_HATCH[tier].asStack())
                .inputItems(wirelessEnergyReceiveCovers[tier - 1].asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ),
                ),
            )
                .inputItems(GTMachines.ENERGY_OUTPUT_HATCH[tier].asStack())
                .inputItems(wirelessEnergyReceiveCovers[tier - 1].asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
        }

        for (tier in GTValues.tiersBetween(GTValues.EV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_4a",
                ),
            )
                .inputItems(GTMachines.ENERGY_INPUT_HATCH_4A[tier].asStack())
                .inputItems(wirelessEnergyReceiveCovers[tier - 1].asStack(2))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_4A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_16a",
                ),
            )
                .inputItems(GTMachines.ENERGY_INPUT_HATCH_16A[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(2))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_16A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_64a",
                ),
            )
                .inputItems(GTMachines.SUBSTATION_ENERGY_INPUT_HATCH[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(4))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(2))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_64A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_4a",
                ),
            )
                .inputItems(GTMachines.ENERGY_OUTPUT_HATCH_4A[tier].asStack())
                .inputItems(wirelessEnergyReceiveCovers[tier - 1].asStack(2))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_4A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_16a",
                ),
            )
                .inputItems(GTMachines.ENERGY_OUTPUT_HATCH_16A[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(2))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_16A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_64a",
                ),
            )
                .inputItems(GTMachines.SUBSTATION_ENERGY_OUTPUT_HATCH[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(4))
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(2))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_64A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
        }

        for (tier in GTValues.tiersBetween(GTValues.IV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_256a",
                ),
            )
                .inputItems(GTMachines.LASER_INPUT_HATCH_256[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(4))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_256A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_1024a",
                ),
            )
                .inputItems(GTMachines.LASER_INPUT_HATCH_1024[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(8))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_1024A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_input_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_4096a",
                ),
            )
                .inputItems(GTMachines.LASER_INPUT_HATCH_4096[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(16))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_4096A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_256a",
                ),
            )
                .inputItems(GTMachines.LASER_OUTPUT_HATCH_256[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(4))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_256A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_1024a",
                ),
            )
                .inputItems(GTMachines.LASER_OUTPUT_HATCH_1024[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(8))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_1024A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)

            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "wireless_energy_output_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ) + "_4096a",
                ),
            )
                .inputItems(GTMachines.LASER_OUTPUT_HATCH_4096[tier].asStack())
                .inputItems(wirelessEnergyReceiveCover4A[tier - 1].asStack(16))
                .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER.asStack())
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack())
                .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack(1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_4096A[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
        }

        for (tier in GTValues.tiersBetween(GTValues.LV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "huge_item_import_bus_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ),
                ),
            )
                .inputItems(GTMachines.ITEM_IMPORT_BUS[tier].asStack())
                .inputItems(if (tier > GTValues.EV) GTMachines.QUANTUM_CHEST[tier] else GTMachines.SUPER_CHEST[tier])
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(CustomMachines.HUGE_ITEM_IMPORT_BUS[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "huge_item_export_bus_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ),
                ),
            )
                .inputItems(GTMachines.ITEM_EXPORT_BUS[tier].asStack())
                .inputItems(if (tier > GTValues.EV) GTMachines.QUANTUM_CHEST[tier] else GTMachines.SUPER_CHEST[tier])
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(CustomMachines.HUGE_ITEM_EXPORT_BUS[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
        }

        for (tier in GTValues.tiersBetween(GTValues.LV, if (GTCEuAPI.isHighTier()) GTValues.OpV else GTValues.UV)) {
            GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(
                GTmm.id(
                    "huge_dual_hatch_" + GTValues.VN[tier].lowercase(
                        Locale.getDefault(),
                    ),
                ),
            )
                .inputItems(CustomMachines.HUGE_ITEM_IMPORT_BUS[tier].asStack())
                .inputItems(if (tier > GTValues.EV) GTMachines.QUANTUM_TANK[tier] else GTMachines.SUPER_TANK[tier])
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(144))
                .outputItems(CustomMachines.HUGE_INPUT_DUAL_HATCH[tier].asStack())
                .duration(200)
                .EUt(GTValues.VA[tier].toLong())
                .save(provider)
        }

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_computation_transmitter_hatch"))
            .inputItems(GTResearchMachines.COMPUTATION_HATCH_TRANSMITTER.asStack())
            .inputItems(CustomTags.ZPM_CIRCUITS)
            .inputItems(GTItems.SENSOR_ZPM)
            .inputFluids(GTMaterials.Polybenzimidazole.getFluid(288))
            .outputItems(WirelessMachines.WIRELESS_COMPUTATION_HATCH_TRANSMITTER)
            .cleanroom(CleanroomType.CLEANROOM)
            .duration(200)
            .EUt(GTValues.VA[GTValues.ZPM].toLong())
            .save(provider)

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTmm.id("wireless_computation_receiver_hatch"))
            .inputItems(GTResearchMachines.COMPUTATION_HATCH_TRANSMITTER.asStack())
            .inputItems(CustomTags.ZPM_CIRCUITS)
            .inputItems(GTItems.EMITTER_ZPM)
            .inputFluids(GTMaterials.Polybenzimidazole.getFluid(288))
            .outputItems(WirelessMachines.WIRELESS_COMPUTATION_HATCH_RECEIVER)
            .cleanroom(CleanroomType.CLEANROOM)
            .duration(200)
            .EUt(GTValues.VA[GTValues.ZPM].toLong())
            .save(provider)
    }
}
