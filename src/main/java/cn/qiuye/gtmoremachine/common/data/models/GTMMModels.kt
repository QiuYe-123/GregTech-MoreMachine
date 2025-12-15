package cn.qiuye.gtmoremachine.common.data.models

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.api.machine.multiblock.IEnergyCommunicationUnitBlock
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock
import cn.qiuye.gtmoremachine.common.block.EnergyCommunicationUnitBlock

import net.minecraft.world.level.block.Block

import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import com.tterrag.registrate.util.nullness.NonNullBiConsumer

object GTMMModels {

    @JvmStatic
    fun createEnergyCommunicationUnitModel(
        energyCommunicationUnitDate: IEnergyCommunicationUnitBlock,
    ): NonNullBiConsumer<DataGenContext<Block, EnergyCommunicationUnitBlock>, RegistrateBlockstateProvider> =
        NonNullBiConsumer {
                ctx: DataGenContext<Block, EnergyCommunicationUnitBlock>,
                prov: RegistrateBlockstateProvider,
            ->
            prov.simpleBlock(
                ctx.getEntry(),
                prov.models()
                    .withExistingParent(
                        "%s_energy_communication_unit".format(
                            energyCommunicationUnitDate.getEnergyCommunicationUnitBlockName(),
                        ),
                        GTmm.id("block/cube/tinted/bottom_top"),
                    )
                    .texture(
                        "bottom",
                        GTmm.id(
                            "block/casings/ecu/%s/bottom".format(
                                energyCommunicationUnitDate.getEnergyCommunicationUnitBlockName(),
                            ),
                        ),
                    )
                    .texture(
                        "top",
                        GTmm.id(
                            "block/casings/ecu/%s/top".format(
                                energyCommunicationUnitDate.getEnergyCommunicationUnitBlockName(),
                            ),
                        ),
                    )
                    .texture(
                        "side",
                        GTmm.id(
                            "block/casings/ecu/%s/side".format(
                                energyCommunicationUnitDate.getEnergyCommunicationUnitBlockName(),
                            ),
                        ),
                    ),
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
