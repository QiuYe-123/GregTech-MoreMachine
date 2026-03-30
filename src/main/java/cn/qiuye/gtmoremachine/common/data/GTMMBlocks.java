package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICCData;
import cn.qiuye.gtmoremachine.api.machine.multiblock.IECUBlock;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock.WECCBlockPartType;
import cn.qiuye.gtmoremachine.common.block.ECUBlock;
import cn.qiuye.gtmoremachine.common.block.ECUBlock.ECUPartType;
import cn.qiuye.gtmoremachine.common.data.models.GTMMModels;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

import com.tterrag.registrate.util.entry.BlockEntry;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;

public class GTMMBlocks {

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    // ECU
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_LV = createEnergyCommunicationUnitBlock(ECUPartType.LV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_MV = createEnergyCommunicationUnitBlock(ECUPartType.MV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_HV = createEnergyCommunicationUnitBlock(ECUPartType.HV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_EV = createEnergyCommunicationUnitBlock(ECUPartType.EV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_IV = createEnergyCommunicationUnitBlock(ECUPartType.IV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_LUV = createEnergyCommunicationUnitBlock(ECUPartType.LUV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_ZPM = createEnergyCommunicationUnitBlock(ECUPartType.ZPM);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UV = createEnergyCommunicationUnitBlock(ECUPartType.UV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UHV = createEnergyCommunicationUnitBlock(ECUPartType.UHV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UEV = createEnergyCommunicationUnitBlock(ECUPartType.UEV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UIV = createEnergyCommunicationUnitBlock(ECUPartType.UIV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UXV = createEnergyCommunicationUnitBlock(ECUPartType.UXV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_OPV = createEnergyCommunicationUnitBlock(ECUPartType.OPV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_MAX = createEnergyCommunicationUnitBlock(ECUPartType.MAX);

    private static BlockEntry<ECUBlock> createEnergyCommunicationUnitBlock(IECUBlock ECUDate) {
        var entry = GTMM
                .block("%s_energy_communication_unit".formatted(ECUDate.getECUBlockName()),
                        e -> new ECUBlock(e, ECUDate))
                .lang("%s Energy Communication Unit".formatted(GTValues.VNF[ECUDate.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate(GTMMModels.createECUModel(ECUDate))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        GTMMAPI.ECU.put(ECUDate, entry);
        return entry;
    }

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    // WECC
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EMPTY_TIER = createCapacityComponentBlock(WECCBlockPartType.EMPTY_TIER);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LV = createCapacityComponentBlock(WECCBlockPartType.LV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MV = createCapacityComponentBlock(WECCBlockPartType.MV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_HV = createCapacityComponentBlock(WECCBlockPartType.HV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EV = createCapacityComponentBlock(WECCBlockPartType.EV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_IV = createCapacityComponentBlock(WECCBlockPartType.IV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LUV = createCapacityComponentBlock(WECCBlockPartType.LuV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_ZPM = createCapacityComponentBlock(WECCBlockPartType.ZPM);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UV = createCapacityComponentBlock(WECCBlockPartType.UV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UHV = createCapacityComponentBlock(WECCBlockPartType.UHV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UEV = createCapacityComponentBlock(WECCBlockPartType.UEV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UIV = createCapacityComponentBlock(WECCBlockPartType.UIV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UXV = createCapacityComponentBlock(WECCBlockPartType.UXV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_OPV = createCapacityComponentBlock(WECCBlockPartType.OpV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MAX = createCapacityComponentBlock(WECCBlockPartType.MAX);

    private static BlockEntry<CapacityComponentBlock> createCapacityComponentBlock(ICCData WECCData) {
        var CapacityComponentBlock = GTMM
                .block("%s_capacity_component".formatted(WECCData.getCCName()),
                        p -> new CapacityComponentBlock(p, WECCData))
                .lang("%s Wireless Energy Capacity Component".formatted(WECCData.getTier() == -1 ? "Empty tier" : GTValues.VNF[WECCData.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, entityType) -> false))
                .blockstate(GTMMModels.createWECCBlockModel(WECCData))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        GTMMAPI.WECC.put(WECCData, CapacityComponentBlock);
        return CapacityComponentBlock;
    }

    public static void init() {}
}
