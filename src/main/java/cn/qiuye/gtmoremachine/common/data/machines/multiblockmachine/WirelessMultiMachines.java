package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine;

import cn.qiuye.gtmoremachine.api.pattern.GTMMPredicates;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DemodulationHubMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DimensionalRelayNodeMachine;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;

public class WirelessMultiMachines {

    public final static MultiblockMachineDefinition WIRELSESS_ENERGY_DIMENSIONAL_RELAY_NODE = GTMMConfig.getINSTANCE().isWirelessDimensionRateEnable ? GTMMREGISTRATE
            .multiblock("wirelsess_energy_dimensional_relay_node", DimensionalRelayNodeMachine::new)
            .langValue("Wirelsess Energy Dimensional Relay Node")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("B")
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("B", GTMMPredicates.EnergyCommunicationUnit())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register() : null;

    public final static MultiblockMachineDefinition WIRELSESS_ENERGY_DEMODULATION_HUB = GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable ? GTMMREGISTRATE
            .multiblock("wirelsess_energy_demodulation_hub", DemodulationHubMachine::new)
            .langValue("Wirelsess Energy Demodulation Hub")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("B")
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("B", GTMMPredicates.WirelessEnergyCapacityComponent())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register() : null;

    public static void init() {}
}
