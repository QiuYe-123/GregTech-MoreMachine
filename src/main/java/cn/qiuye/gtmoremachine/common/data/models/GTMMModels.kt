package cn.qiuye.gtmoremachine.common.data.models

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock

import net.minecraft.world.level.block.Block

import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import com.tterrag.registrate.util.nullness.NonNullBiConsumer

object GTMMModels {

    @JvmStatic
    fun createCapacityComponentBlockModel(
        capacityComponentData: ICapacityComponentData,
    ): NonNullBiConsumer<DataGenContext<Block, CapacityComponentBlock>, RegistrateBlockstateProvider> =
        NonNullBiConsumer {
                ctx: DataGenContext<Block, CapacityComponentBlock>,
                prov: RegistrateBlockstateProvider,
            ->
            prov.simpleBlock(
                ctx.getEntry(),
                prov.models().cubeBottomTop(
                    ctx.name,
                    GTmm.id(
                        "block/casings/capacitycomponent/" + capacityComponentData.getCapacityComponentName() + "/side",
                    ),
                    GTmm.id(
                        "block/casings/capacitycomponent/" + capacityComponentData.getCapacityComponentName() + "/top",
                    ),
                    GTmm.id(
                        "block/casings/capacitycomponent/" + capacityComponentData.getCapacityComponentName() + "/top",
                    ),
                ),
            )
        }
}
