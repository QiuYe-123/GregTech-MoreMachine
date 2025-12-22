package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;
import cn.qiuye.gtmoremachine.api.machine.multiblock.IEnergyCommunicationUnitBlock;
import cn.qiuye.gtmoremachine.common.block.BlockMap;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock.CapacityComponentBlockPartType;
import cn.qiuye.gtmoremachine.common.block.EnergyCommunicationUnitBlock;
import cn.qiuye.gtmoremachine.common.block.EnergyCommunicationUnitBlock.EnergyCommunicationUnitPartType;
import cn.qiuye.gtmoremachine.common.data.models.GTMMModels;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

import com.tterrag.registrate.util.entry.BlockEntry;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class GTMMBlocks {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    // ECU
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_LV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.LV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_MV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.MV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_HV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.HV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_EV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.EV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_IV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.IV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_LUV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.LUV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_ZPM = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.ZPM);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_UV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.UV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_UHV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.UHV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_UEV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.UEV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_UIV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.UIV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_UXV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.UXV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_OPV = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.OPV);
    public static final BlockEntry<EnergyCommunicationUnitBlock> ENERGY_COMMUNICATION_UNIT_MAX = createEnergyCommunicationUnitBlock(EnergyCommunicationUnitPartType.MAX);

    private static BlockEntry<EnergyCommunicationUnitBlock> createEnergyCommunicationUnitBlock(IEnergyCommunicationUnitBlock EnergyCommunicationUnitDate) {
        var entry = GTMMREGISTRATE
                .block("%s_energy_communication_unit".formatted(EnergyCommunicationUnitDate.getEnergyCommunicationUnitBlockName()),
                        e -> new EnergyCommunicationUnitBlock(e, EnergyCommunicationUnitDate))
                .lang("%s Energy Communication Unit".formatted(GTValues.VNF[EnergyCommunicationUnitDate.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate(GTMMModels.createEnergyCommunicationUnitModel(EnergyCommunicationUnitDate))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        GTMMAPI.ECU.put(EnergyCommunicationUnitDate, entry);
        return entry;
    }

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    // WECC
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EMPTY_TIER = createCapacityComponentBlock(CapacityComponentBlockPartType.EMPTY_TIER);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LV = createCapacityComponentBlock(CapacityComponentBlockPartType.LV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MV = createCapacityComponentBlock(CapacityComponentBlockPartType.MV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_HV = createCapacityComponentBlock(CapacityComponentBlockPartType.HV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EV = createCapacityComponentBlock(CapacityComponentBlockPartType.EV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_IV = createCapacityComponentBlock(CapacityComponentBlockPartType.IV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LUV = createCapacityComponentBlock(CapacityComponentBlockPartType.LuV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_ZPM = createCapacityComponentBlock(CapacityComponentBlockPartType.ZPM);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UV = createCapacityComponentBlock(CapacityComponentBlockPartType.UV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UHV = createCapacityComponentBlock(CapacityComponentBlockPartType.UHV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UEV = createCapacityComponentBlock(CapacityComponentBlockPartType.UEV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UIV = createCapacityComponentBlock(CapacityComponentBlockPartType.UIV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UXV = createCapacityComponentBlock(CapacityComponentBlockPartType.UXV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_OPV = createCapacityComponentBlock(CapacityComponentBlockPartType.OpV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MAX = createCapacityComponentBlock(CapacityComponentBlockPartType.MAX);

    private static BlockEntry<CapacityComponentBlock> createCapacityComponentBlock(ICapacityComponentData CapacityComponentData) {
        var CapacityComponentBlock = GTMMREGISTRATE
                .block("%s_capacity_component".formatted(CapacityComponentData.getCapacityComponentName()),
                        p -> new CapacityComponentBlock(p, CapacityComponentData))
                .lang("%s Wireless Energy Capacity Component".formatted(CapacityComponentData.getTier() == -1 ? "Empty tier" : GTValues.VNF[CapacityComponentData.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, entityType) -> false))
                .blockstate(GTMMModels.createCapacityComponentBlockModel(CapacityComponentData))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
        GTMMAPI.WECC.put(CapacityComponentData, CapacityComponentBlock);
        return CapacityComponentBlock;
    }

    public static void init() {
        BlockMap.init();
        int j = 0;
        for (int i = 3; i <= 13; i++) {
            BlockMap.rotMap.put(j, GTMachines.ROTOR_HOLDER[i]);
            j++;
        }
    }
}
