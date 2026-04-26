package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
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
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.*;

@GTMMDataGeneratorScanned
public class CustomMachines {

    private static final String HUGE_ITEM_BUS_PREFIX = "gtmoremachine.machine.huge_item_bus";
    private static final String HUGE_DUAL_HATCH_PREFIX = "gtmoremachine.machine.huge_dual_hatch";
    private static final String SHARE_INVENTORY_PREFIX = "gui.gtmoremachine.share_inventory";
    @GTMMRegisterLanguage(en = "Inputs items for multiblock structures, with each slot able to store up to 2^31-1 items.", cn = "为多方块结构输入物品，每个槽位最多可存储2^31-1个物品。")
    public static final String HUGE_ITEM_BUS_IMPORT_TOOLTIP = HUGE_ITEM_BUS_PREFIX + ".import.tooltip";
    @GTMMRegisterLanguage(en = "Outputs items for multiblock structures, with each slot able to store up to 2^31-1 items.", cn = "为多方块结构输出物品，每个槽位最多可存储2^31-1个物品。")
    public static final String HUGE_ITEM_BUS_EXPORT_TOOLTIP = HUGE_ITEM_BUS_PREFIX + ".export.tooltip";
    @GTMMRegisterLanguage(en = "Returns all items to the container in front.", cn = "退回所有物品到面前的容器中。")
    public static final String HUGE_ITEM_BUS_TOOLTIP_1 = HUGE_ITEM_BUS_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Item slots: %s/%s", cn = "物品槽位: %s/%s")
    public static final String HUGE_ITEM_BUS_TOOLTIP_2 = HUGE_ITEM_BUS_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Empty", cn = "空")
    public static final String HUGE_ITEM_BUS_TOOLTIP_3 = HUGE_ITEM_BUS_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "Fluid slots: %s/%s", cn = "流体槽位: %s/%s")
    public static final String HUGE_DUAL_HATCH_TOOLTIP_2 = HUGE_DUAL_HATCH_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Catalyst", cn = "催化剂")
    public static final String SHARE_INVENTORY_TITLE = SHARE_INVENTORY_PREFIX + ".title";
    @GTMMRegisterLanguage(en = "Open Catalyst Slot", cn = "打开催化剂槽")
    public static final String SHARE_INVENTORY_DESC_0 = SHARE_INVENTORY_PREFIX + ".desc.0";
    @GTMMRegisterLanguage(en = "In the catalyst slot, only non-consumable items can participate in the synthesis.", cn = "在催化剂槽中只有不消耗的物品才能参与合成。")
    public static final String SHARE_INVENTORY_DESC_1 = SHARE_INVENTORY_PREFIX + ".desc.1";
    @GTMMRegisterLanguage(en = "Common items can be automatically input by placing a container in front of the input bus.", cn = "普通物品请通过在输入总线面前放置容器以自动输入。")
    public static final String SHARE_INVENTORY_DESC_2 = SHARE_INVENTORY_PREFIX + ".desc.2";

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.ULV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV);

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
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
                    .tooltips(Component.translatable(HUGE_ITEM_BUS_IMPORT_TOOLTIP),
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
                    .tooltips(Component.translatable(HUGE_ITEM_BUS_EXPORT_TOOLTIP),
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

    public static void init() {}
}
