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
    fun createEnergyCommunicationUnitModel(
        tierName: String,
    ): NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateBlockstateProvider> = NonNullBiConsumer {
            ctx: DataGenContext<Block, Block>,
            prov: RegistrateBlockstateProvider,
        ->
        prov.simpleBlock(
            ctx.getEntry(),
            prov.models()
                .withExistingParent(
                    "%s_energy_communication_unit".format(tierName),
                    GTmm.id("block/cube/tinted/bottom_top"),
                )
                .texture("bottom", GTmm.id("block/casings/ecu/%s/bottom".format(tierName)))
                .texture("top", GTmm.id("block/casings/ecu/%s/top".format(tierName)))
                .texture("side", GTmm.id("block/casings/ecu/%s/side".format(tierName))),
        )
    }

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
