package cn.qiuye.gtmoremachine.common.block

import cn.qiuye.gtmoremachine.api.GTMMAPI
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICCData

import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.utils.FormattingUtil

import net.minecraft.MethodsReturnNonnullByDefault
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block

import lombok.Getter

import java.math.BigInteger
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CapacityComponentBlock(properties: Properties, @field:Getter val data: ICCData) : Block(properties) {
	override fun appendHoverText(stack: ItemStack, level: BlockGetter?, tooltip: MutableList<Component>, flag: TooltipFlag) {
		super.appendHoverText(stack, level, tooltip, flag)
		if (this.data.getTier() == -1) {
			tooltip.add(Component.translatable("block.gtmoremachine.capacity_component.tooltip_empty"))
		} else {
			tooltip.add(
				Component.translatable(
					"block.gtmoremachine.capacity_component.tooltip_filled",
					FormattingUtil.formatNumbers(this.data.getCapacity()),
				),
			)
			tooltip.add(
				Component.translatable(
					"block.gtmoremachine.capacity_component.tooltip_passive_drain",
					FormattingUtil.formatNumbers(this.data.getLossEnergy()),
				),
			)
		}
	}

	@MethodsReturnNonnullByDefault
	enum class WECCBlockPartType :
		StringRepresentable,
		ICCData {
		EMPTY_TIER,
		LV(GTValues.LV),
		MV(GTValues.MV),
		HV(GTValues.HV),
		EV(GTValues.EV),
		IV(GTValues.IV),
		LuV(GTValues.LuV),
		ZPM(GTValues.ZPM),
		UV(GTValues.UV),
		UHV(GTValues.UHV),
		UEV(GTValues.UEV),
		UIV(GTValues.UIV),
		UXV(GTValues.UXV),
		OpV(GTValues.OpV),
		MAX(GTValues.MAX),
		;

		private val tier: Int
		private val capacity: BigInteger
		private val lossEnergy: BigInteger

		constructor() {
			this.tier = -1
			this.capacity = BigInteger.ZERO
			this.lossEnergy = BigInteger.ZERO
		}

		constructor(tier: Int) {
			this.tier = tier
			this.capacity = GTMMAPI.capacityComponentBlock(tier, true)
			this.lossEnergy = GTMMAPI.capacityComponentBlock(tier, false)
		}

		override fun getTier(): Int = tier

		override fun getCapacity(): BigInteger = capacity

		override fun getLossEnergy(): BigInteger = lossEnergy

		override fun getCCName(): String = name.lowercase()

		override fun getSerializedName(): String = ccName
	}
}
