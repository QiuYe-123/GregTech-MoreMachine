package cn.qiuye.gtmoremachine.common.data.models

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICCData
import cn.qiuye.gtmoremachine.api.machine.multiblock.IECUBlock
import cn.qiuye.gtmoremachine.common.block.ECUBlock
import cn.qiuye.gtmoremachine.common.block.WECCBlock

import net.minecraft.world.level.block.Block

import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import com.tterrag.registrate.util.nullness.NonNullBiConsumer

object GTMMModels {

	@JvmStatic
	fun createECUModel(ecuBlockDate: IECUBlock): NonNullBiConsumer<DataGenContext<Block, ECUBlock>, RegistrateBlockstateProvider> =
		NonNullBiConsumer { ctx: DataGenContext<Block, ECUBlock>, prov: RegistrateBlockstateProvider ->
			prov.simpleBlock(
				ctx.getEntry(),
				prov.models().withExistingParent(
					"%s_energy_communication_unit".format(ecuBlockDate.getECUBlockName()),
					GTmm.id("block/cube/tinted/bottom_top"),
				)
					.texture("bottom", GTmm.id("block/casings/ecu/%s/bottom".format(ecuBlockDate.getECUBlockName())))
					.texture("top", GTmm.id("block/casings/ecu/%s/top".format(ecuBlockDate.getECUBlockName())))
					.texture("side", GTmm.id("block/casings/ecu/%s/side".format(ecuBlockDate.getECUBlockName()))),
			)
		}

	@JvmStatic
	fun createWECCBlockModel(weccData: ICCData): NonNullBiConsumer<DataGenContext<Block, WECCBlock>, RegistrateBlockstateProvider> =
		NonNullBiConsumer { ctx: DataGenContext<Block, WECCBlock>, prov: RegistrateBlockstateProvider ->
			prov.simpleBlock(
				ctx.getEntry(),
				prov.models().cubeBottomTop(
					ctx.name,
					GTmm.id("block/casings/capacitycomponent/" + weccData.getCCName() + "/side"),
					GTmm.id("block/casings/capacitycomponent/" + weccData.getCCName() + "/top"),
					GTmm.id("block/casings/capacitycomponent/" + weccData.getCCName() + "/top"),
				),
			)
		}
}
