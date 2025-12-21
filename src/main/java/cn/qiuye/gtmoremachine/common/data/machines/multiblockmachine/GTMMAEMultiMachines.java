package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;

public class GTMMAEMultiMachines {

    // TODO: 未进行具体实现和多方块结构空缺
    public static final MultiblockMachineDefinition ME_MOLECULAR_ASSEMBLY_CENTER = GTMMREGISTRATE
            .multiblock("me_molecular_assembly_center", WorkableElectricMultiblockMachine::new)
            .langValue("ME Molecular Assembly Center")
            .rotationState(RotationState.ALL)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public static final MultiblockMachineDefinition ME_SYNTHESIS_COMPUTING_CENTER = GTMMREGISTRATE
            .multiblock("me_synthesis_computing_center", WorkableElectricMultiblockMachine::new)
            .langValue("ME Synthesis Computing Center")
            .rotationState(RotationState.ALL)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public static final MultiblockMachineDefinition ME_STORAGE_CENTER = GTMMREGISTRATE
            .multiblock("me_storage_center", WorkableElectricMultiblockMachine::new)
            .langValue("ME Storage Center")
            .rotationState(RotationState.ALL)
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("A")
                    .aisle("~")
                    .where("~", controller(blocks(definition.getBlock())))
                    .where("A", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/hpca/high_power_casing"),
                    GTCEu.id("block/multiblock/network_switch"))
            .register();

    public static void init() {}
}
