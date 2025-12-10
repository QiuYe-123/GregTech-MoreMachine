package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;
import cn.qiuye.gtmoremachine.common.block.BlockMap;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock;
import cn.qiuye.gtmoremachine.common.data.models.GTMMModels;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

import com.tterrag.registrate.util.entry.BlockEntry;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class GTMMBlocks {

    // WECC
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EMPTY_TIER = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.EMPTY_TIER);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.LV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.MV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_HV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.HV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_EV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.EV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_IV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.IV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_LUV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.LuV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_ZPM = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.ZPM);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.UV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UHV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.UHV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UEV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.UEV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UIV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.UIV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_UXV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.UXV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_OPV = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.OpV);
    public static final BlockEntry<CapacityComponentBlock> CAPACITYCOMPONENT_MAX = createCapacityComponentBlock(CapacityComponentBlock.CapacityComponentBlockPartType.MAX);

    private static BlockEntry<CapacityComponentBlock> createCapacityComponentBlock(ICapacityComponentData CapacityComponentData) {
        var CapacityComponentBlock = GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable ? GTMMREGISTRATE
                .block("%s_capacity_component".formatted(CapacityComponentData.getCapacityComponentName()),
                        p -> new CapacityComponentBlock(p, CapacityComponentData))
                .lang("%s Wireless Energy Capacity Component".formatted(CapacityComponentData.getTier() == -1 ? "Empty tier" : GTValues.VNF[CapacityComponentData.getTier()]))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, entityType) -> false))
                .blockstate(GTMMModels.createCapacityComponentBlockModel(CapacityComponentData))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register() : null;
        if (GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable && CapacityComponentBlock != null) {
            GTMMAPI.WECC.put(CapacityComponentData, CapacityComponentBlock);
        }
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
