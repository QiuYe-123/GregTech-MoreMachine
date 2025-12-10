package cn.qiuye.gtmoremachine.common.data.models;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock;

import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

public class GTMMModels {

    public static NonNullBiConsumer<DataGenContext<Block, CapacityComponentBlock>, RegistrateBlockstateProvider> createCapacityComponentBlockModel(ICapacityComponentData CapacityComponentData) {
        return (ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().cubeBottomTop(ctx.getName(),
                GTmm.id("block/casings/capacitycomponent/" + CapacityComponentData.getCapacityComponentName() + "/side"),
                GTmm.id("block/casings/capacitycomponent/" + CapacityComponentData.getCapacityComponentName() + "/top"),
                GTmm.id("block/casings/capacitycomponent/" + CapacityComponentData.getCapacityComponentName() + "/top")));
    }
}
