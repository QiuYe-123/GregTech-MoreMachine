package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.ICCData;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.IECUBlock;
import cn.qiuye.gtmoremachine.common.block.ECUBlock;
import cn.qiuye.gtmoremachine.common.block.ECUBlock.ECUPartType;
import cn.qiuye.gtmoremachine.common.block.WECCBlock;
import cn.qiuye.gtmoremachine.common.block.WECCBlock.WECCBlockPartType;
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
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_LV = createECUBlock(ECUPartType.LV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_MV = createECUBlock(ECUPartType.MV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_HV = createECUBlock(ECUPartType.HV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_EV = createECUBlock(ECUPartType.EV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_IV = createECUBlock(ECUPartType.IV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_LUV = createECUBlock(ECUPartType.LUV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_ZPM = createECUBlock(ECUPartType.ZPM);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UV = createECUBlock(ECUPartType.UV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UHV = createECUBlock(ECUPartType.UHV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UEV = createECUBlock(ECUPartType.UEV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UIV = createECUBlock(ECUPartType.UIV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_UXV = createECUBlock(ECUPartType.UXV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_OPV = createECUBlock(ECUPartType.OPV);
    public static final BlockEntry<ECUBlock> ENERGY_COMMUNICATION_UNIT_MAX = createECUBlock(ECUPartType.MAX);

    private static BlockEntry<ECUBlock> createECUBlock(IECUBlock ECUDate) {
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
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_EMPTY_TIER = createWECCBlock(WECCBlockPartType.EMPTY_TIER);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_LV = createWECCBlock(WECCBlockPartType.LV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_MV = createWECCBlock(WECCBlockPartType.MV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_HV = createWECCBlock(WECCBlockPartType.HV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_EV = createWECCBlock(WECCBlockPartType.EV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_IV = createWECCBlock(WECCBlockPartType.IV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_LUV = createWECCBlock(WECCBlockPartType.LuV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_ZPM = createWECCBlock(WECCBlockPartType.ZPM);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_UV = createWECCBlock(WECCBlockPartType.UV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_UHV = createWECCBlock(WECCBlockPartType.UHV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_UEV = createWECCBlock(WECCBlockPartType.UEV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_UIV = createWECCBlock(WECCBlockPartType.UIV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_UXV = createWECCBlock(WECCBlockPartType.UXV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_OPV = createWECCBlock(WECCBlockPartType.OpV);
    public static final BlockEntry<WECCBlock> CAPACITYCOMPONENT_MAX = createWECCBlock(WECCBlockPartType.MAX);

    private static BlockEntry<WECCBlock> createWECCBlock(ICCData WECCData) {
        var WECCBlock = GTMM
                .block("%s_capacity_component".formatted(WECCData.getCCName()),
                        p -> new WECCBlock(p, WECCData))
                .lang("%s Wireless Energy Capacity Component".formatted(WECCData.getTier() == -1 ? "Empty tier" : GTValues.VNF[WECCData.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, entityType) -> false))
                .blockstate(GTMMModels.createWECCBlockModel(WECCData))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        GTMMAPI.WECC.put(WECCData, WECCBlock);
        return WECCBlock;
    }

    public static void init() {}
}
