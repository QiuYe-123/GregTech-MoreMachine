package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine;

import cn.qiuye.gtmoremachine.common.data.machines.multiblock.planetaryengine.PlanetaryEngineMultiblock;
import cn.qiuye.gtmoremachine.utils.GetRegistries;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.HIGH_POWER_CASING;

public class GTMMMultiblockMachineA {

    public static final MultiblockMachineDefinition PlanetaryEngine = GTMM.multiblock("planetaryengine", WorkableElectricMultiblockMachine::new)
            .langValue("§cPlanetary Engine")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.ALLOY_SMELTER_RECIPES)
            .appearanceBlock(HIGH_POWER_CASING)
            .pattern(definition -> PlanetaryEngineMultiblock.PATTERN
                    .where('~', controller(blocks(definition.getBlock())))
                    .where(' ', any())
                    .where('A', blocks(Blocks.PRISMARINE)
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where('B', blocks(GetRegistries.getBlock("gtlcore:iridium_casing")))
                    .where('C', blocks(GTBlocks.COMPUTER_CASING.get()))
                    .where('D', blocks(GetRegistries.getBlock("gtlcore:oxidation_resistant_hastelloy_n_mechanical_casing")))
                    .where('E', blocks(GetRegistries.getBlock("gtlcore:hyper_mechanical_casing")))
                    .where('F', blocks(GetRegistries.getBlock("gtlcore:fission_reactor_casing")))
                    .where('G', blocks(GetRegistries.getBlock("gtlcore:space_elevator_mechanical_casing")))
                    .where('H', blocks(GetRegistries.getBlock("gtlcore:antifreeze_heatproof_machine_casing")))
                    .where('I', blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                    .where('J', blocks(GetRegistries.getBlock("gtlcore:dimension_injection_casing")))
                    .where('K', blocks(GTBlocks.CASING_GRATE.get()))
                    .where('L', blocks(GCYMBlocks.CASING_CORROSION_PROOF.get()))
                    .where('M', blocks(GetRegistries.getBlock("kubejs:abyssalalloy_coil_block")))
                    .where('N', blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('O', blocks(GetRegistries.getBlock("gtlcore:dragon_strength_tritanium_casing")))
                    .where('P', blocks(GetRegistries.getBlock("gtlcore:dimensionally_transcendent_casing")))
                    .where('Q', blocks(GetRegistries.getBlock("gtlcore:hsss_reinforced_borosilicate_glass")))
                    .where('R', blocks(GetRegistries.getBlock("gtlcore:improved_superconductor_coil")))
                    .where('S', blocks(Blocks.IRON_BARS))
                    .where('T', fluids(Fluids.LAVA))
                    .where('U', blocks(GetRegistries.getBlock("gtlcore:hyper_core")))
                    .where('V', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('W', blocks(GetRegistries.getBlock("gtlcore:enhance_hyper_mechanical_casing")))
                    .where('X', blocks(GCYMBlocks.CASING_NONCONDUCTING.get()))
                    .where('Y', blocks(GCYMBlocks.HEAT_VENT.get()))
                    .where('Z', blocks(GetRegistries.getBlock("gtlcore:degenerate_rhenium_constrained_casing")))
                    .where('a', blocks(GetRegistries.getBlock("gtlcore:naquadah_alloy_casing")))
                    .where('b', blocks(GetRegistries.getBlock("gtlcore:power_module_4")))
                    .where('c', blocks(GetRegistries.getBlock("gtlcore:lafium_mechanical_casing")))
                    .where('d', blocks(GetRegistries.getBlock("gtlcore:molecular_casing")))
                    .where('e', blocks(GetRegistries.getBlock("gtlcore:sps_casing")))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public static void init() {}
}
