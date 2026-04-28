package cn.qiuye.gtmoremachine.common.item.behaviour

import cn.qiuye.gtmoremachine.common.data.GTMMDataComponents

import com.gregtechceu.gtceu.api.item.component.IAddInformation

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

import java.util.function.Consumer

/**
 * @param tooltips a consumer adding translated tooltips to the tooltip list
 */
class WirelessTransferCoverTooltipBehavior(private val tooltips: Consumer<MutableList<Component>>) : IAddInformation {
	override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, isAdvanced: TooltipFlag) {
		val data = stack.get(GTMMDataComponents.WIRELESS_TRANSFER_COVER.get())
		if (data != null && data.isBound()) {
			val lst: MutableList<Component> = ArrayList()
			lst.add(
				Component.translatable(
					WirelessTransferCoverPlaceBehavior.WIRELESS_TRANSFER_TOOLTIP_1,
					Component.translatable(data.blockId()),
					data.shortPos(),
				),
			)
			tooltipComponents.addAll(lst)
		}
		tooltips.accept(tooltipComponents)
	}
}
