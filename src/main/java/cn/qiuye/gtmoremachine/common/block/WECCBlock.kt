package cn.qiuye.gtmoremachine.common.block

import cn.qiuye.gtmoremachine.api.GTMMAPI
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.ICCData

import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.utils.FormattingUtil

import net.minecraft.MethodsReturnNonnullByDefault
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

import lombok.Getter

import java.math.BigInteger

@GTMMDataGeneratorScanned
@MethodsReturnNonnullByDefault
class WECCBlock(properties: Properties, @field:Getter val data: ICCData) : Block(properties) {

	companion object {
		private const val CAPACITY_COMPONENT_PREFIX: String = "block.gtmoremachine.capacity_component"

		@GTMMRegisterLanguage(
			en = "§7For filling structural gaps in the Wireless Energy Storage Module",
			cn = "§7用于填补无线电网解调枢纽的结构空隙",
		)
		const val CAPACITY_COMPONENT_TOOLTIP_EMPTY: String = "$CAPACITY_COMPONENT_PREFIX.tooltip_empty"

		@GTMMRegisterLanguage(en = "§cCapacity component capacity: §f%d EU", cn = "§c容量组件容量：§f%d EU")
		const val CAPACITY_COMPONENT_TOOLTIP_FILLED: String = "$CAPACITY_COMPONENT_PREFIX.tooltip_filled"

		@GTMMRegisterLanguage(
			en = "§cCapacity component passive energy consumption: §f%d EU",
			cn = "§c容量组件被动耗能：§f%d EU",
		)
		const val CAPACITY_COMPONENT_TOOLTIP_PASSIVE_DRAIN: String = "$CAPACITY_COMPONENT_PREFIX.tooltip_passive_drain"
	}
	override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
		super.appendHoverText(stack, context, tooltip, flag)
		if (this.data.getTier() == -1) {
			tooltip.add(Component.translatable(CAPACITY_COMPONENT_TOOLTIP_EMPTY))
		} else {
			tooltip.add(Component.translatable(CAPACITY_COMPONENT_TOOLTIP_FILLED, FormattingUtil.formatNumbers(this.data.getCapacity())))
			tooltip.add(Component.translatable(CAPACITY_COMPONENT_TOOLTIP_PASSIVE_DRAIN, FormattingUtil.formatNumbers(this.data.getLossEnergy())))
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
