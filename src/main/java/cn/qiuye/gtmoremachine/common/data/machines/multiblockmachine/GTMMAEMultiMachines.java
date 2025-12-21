package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine;

import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.integration.ae.machine.multiblock.electric.MEMolecularAssemblyCenter;
import cn.qiuye.gtmoremachine.integration.ae.machine.multiblock.electric.MEStotageCenter;
import cn.qiuye.gtmoremachine.integration.ae.machine.multiblock.electric.MESynthesisDistributionCenter;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;

public class GTMMAEMultiMachines {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    // TODO: 未进行具体实现和多方块结构空缺
    public static final MultiblockMachineDefinition ME_MOLECULAR_ASSEMBLY_CENTER = GTMMREGISTRATE
            .multiblock("me_molecular_assembly_center", MEMolecularAssemblyCenter::new)
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
            .multiblock("me_synthesis_computing_center", MESynthesisDistributionCenter::new)
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
            .multiblock("me_storage_center", MEStotageCenter::new)
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
