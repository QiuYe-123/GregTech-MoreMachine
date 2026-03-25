package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine;

import cn.qiuye.gtmoremachine.api.pattern.GTMMPredicates;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DemodulationHubMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DimensionalRelayNodeMachine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;

public class WirelessMultiMachines {

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public final static MultiblockMachineDefinition WIRELSESS_ENERGY_DIMENSIONAL_RELAY_NODE = GTMM
            .multiblock("wirelsess_energy_dimensional_relay_node", DimensionalRelayNodeMachine::new)
            .langValue("Wirelsess Energy Dimensional Relay Node")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", GTMMPredicates.EnergyCommunicationUnit())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public final static MultiblockMachineDefinition WIRELSESS_ENERGY_DEMODULATION_HUB = GTMM
            .multiblock("wirelsess_energy_demodulation_hub", DemodulationHubMachine::new)
            .langValue("Wirelsess Energy Demodulation Hub")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", GTMMPredicates.WirelessEnergyCapacityComponent())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public static void init() {}
}
