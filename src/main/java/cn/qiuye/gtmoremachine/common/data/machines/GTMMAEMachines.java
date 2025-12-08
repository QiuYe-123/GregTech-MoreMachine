package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.integration.ae.machine.MEOutputPartMachine;
import cn.qiuye.gtmoremachine.integration.ae.machine.ProgrammableDualHatchPartMachine;
import cn.qiuye.gtmoremachine.integration.ae.machine.ProgrammableHatchPartMachine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;

import static cn.qiuye.gtmoremachine.common.data.machines.utils.CustomMachinesUtils.registerTieredMachines;
import static cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeBusPartMachine.INV_MULTIPLE;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_HATCH_TIERS;

public class GTMMAEMachines {

    public static final MachineDefinition ME_EXPORT_BUFFER = GTMMREGISTRATE
            .machine("me_export_buffer", MEOutputPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS, PartAbility.EXPORT_FLUIDS)
            .colorOverlayTieredHullModel(GTmm.id("block/overlay/appeng/me_output_bus"))
            .tooltips(Component.translatable("gtceu.part_sharing.enabled"))
            .tier(GTValues.LuV)
            .register();

    public static final MachineDefinition[] PROGRAMMABLEC_HATCH = registerTieredMachines(
            "programmablec_hatch", (holder, tier) -> new ProgrammableHatchPartMachine(holder, tier, IO.IN),
            (tier, builder) -> builder
                    .langValue("%s Programmablec Hatch".formatted(GTValues.VNF[tier]))
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS)
                    .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                    .workableTieredHullModel(GTCEu.id("block/machine/part/dual_hatch.import"))
                    .tooltips(Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.item_storage_capacity", (int) Math.pow((tier - 4), 2)),
                            Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity_mult", (tier - 4), DualHatchPartMachine.getTankCapacity(DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier)),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register(),
            DUAL_HATCH_TIERS);

    public static final MachineDefinition[] PROGRAMMABLEC_DUALHATCH = registerTieredMachines(
            "programmablec_dualhatch", (holder, tier) -> new ProgrammableDualHatchPartMachine(holder, tier, IO.IN),
            (tier, builder) -> builder
                    .langValue("%s Programmablec Dual Hatch".formatted(GTValues.VNF[tier]))
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS)
                    .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                    .workableTieredHullModel(GTCEu.id("block/machine/part/dual_hatch.import"))
                    .tooltips(Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.item_storage_capacity", (1 + tier) * INV_MULTIPLE),
                            Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity_mult", tier, FormattingUtil.formatNumbers(Integer.MAX_VALUE)),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register(),
            DUAL_HATCH_TIERS);

    public static void init() {
        GTMMAEMultiMachines.init();
    }
}
