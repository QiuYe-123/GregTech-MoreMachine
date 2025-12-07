package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeBusPartMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeDualHatchPartMachine;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;

import static cn.qiuye.gtmoremachine.common.data.machines.utils.CustomMachinesUtils.registerTieredMachines;
import static cn.qiuye.gtmoremachine.common.machine.multiblock.part.HugeBusPartMachine.INV_MULTIPLE;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.*;

public class CustomMachines {

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.ULV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV);

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    public static final MachineDefinition[] HUGE_ITEM_IMPORT_BUS = registerTieredMachines("huge_item_import_bus",
            (holder, tier) -> new HugeBusPartMachine(holder, tier, IO.IN),
            (tier, builder) -> builder
                    .langValue(GTValues.VNF[tier] + " Input Bus")
                    .rotationState(RotationState.ALL)
                    .abilities(
                            tier == 0 ? new PartAbility[] { PartAbility.IMPORT_ITEMS, PartAbility.STEAM_IMPORT_ITEMS } :
                                    new PartAbility[] { PartAbility.IMPORT_ITEMS })
                    .colorOverlayTieredHullModel("overlay_pipe_in_emissive", "overlay_pipe", OVERLAY_ITEM_HATCH_INPUT)
                    .tooltips(Component.translatable("gtmoremachine.machine.huge_item_bus.import.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                                    (1 + tier) * HugeBusPartMachine.INV_MULTIPLE))
                    .tooltips(Component.translatable("gtceu.part_sharing.enabled"))
                    .register(),
            ALL_TIERS);

    public static final MachineDefinition[] HUGE_ITEM_EXPORT_BUS = registerTieredMachines("huge_item_export_bus",
            (holder, tier) -> new HugeBusPartMachine(holder, tier, IO.OUT),
            (tier, builder) -> builder
                    .langValue(GTValues.VNF[tier] + " Output Bus")
                    .rotationState(RotationState.ALL)
                    .abilities(
                            tier == 0 ? new PartAbility[] { PartAbility.EXPORT_ITEMS, PartAbility.STEAM_EXPORT_ITEMS } :
                                    new PartAbility[] { PartAbility.EXPORT_ITEMS })
                    .colorOverlayTieredHullModel("overlay_pipe_out_emissive", "overlay_pipe", OVERLAY_ITEM_HATCH_OUTPUT)
                    .tooltips(Component.translatable("gtmoremachine.machine.huge_item_bus.export.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                                    (1 + tier) * INV_MULTIPLE))
                    .tooltips(Component.translatable("gtceu.part_sharing.enabled"))
                    .register(),
            ALL_TIERS);

    public static final MachineDefinition[] HUGE_INPUT_DUAL_HATCH = registerTieredMachines("huge_dual_hatch",
            (holder, tier) -> new HugeDualHatchPartMachine(holder, tier, IO.IN),
            (tier, builder) -> {
                builder.langValue(GTValues.VNF[tier] + " Huge Input Dual Hatch")
                        .rotationState(RotationState.ALL)
                        .overlayTieredHullModel("dual_input_hatch")
                        .abilities(GTMachineUtils.DUAL_INPUT_HATCH_ABILITIES)
                        .tooltips(Component.translatable("gtceu.machine.dual_hatch.import.tooltip"));
                builder.tooltips(Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                        (1 + tier) * INV_MULTIPLE))
                        .tooltips(Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                tier, FormattingUtil.formatNumbers(Integer.MAX_VALUE)))
                        .tooltips(Component.translatable("gtceu.part_sharing.enabled"));
                return builder.register();
            },
            ALL_TIERS);

    public static void init() {
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEMachines.init();
        }
    }
}
