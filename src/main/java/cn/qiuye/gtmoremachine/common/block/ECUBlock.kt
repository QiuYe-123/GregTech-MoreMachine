package cn.qiuye.gtmoremachine.common.block

import cn.qiuye.gtmoremachine.api.machine.multiblock.IECUBlock

import com.gregtechceu.gtceu.api.GTValues

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block

import lombok.Getter

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ECUBlock(properties: Properties, @field:Getter val data: IECUBlock) : Block(properties) {
	enum class ECUPartType :
		StringRepresentable,
		IECUBlock {
		LV(GTValues.LV),
		MV(GTValues.MV),
		HV(GTValues.HV),
		EV(GTValues.EV),
		IV(GTValues.IV),
		LUV(GTValues.LuV),
		ZPM(GTValues.ZPM),
		UV(GTValues.UV),
		UHV(GTValues.UHV),
		UEV(GTValues.UEV),
		UIV(GTValues.UIV),
		UXV(GTValues.UXV),
		OPV(GTValues.OpV),
		MAX(GTValues.MAX),
		;

		private val tier: Int
		constructor(tier: Int) {
			this.tier = tier
		}
		override fun getTier(): Int = tier
		override fun getECUBlockName(): String = name.lowercase()
		override fun getSerializedName(): String = getECUBlockName()
	}
}
