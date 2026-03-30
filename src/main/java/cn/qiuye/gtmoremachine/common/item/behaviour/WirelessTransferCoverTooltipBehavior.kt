package cn.qiuye.gtmoremachine.common.item.behaviour

import com.gregtechceu.gtceu.api.item.component.IAddInformation

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

import java.util.function.Consumer

/**
 * @param tooltips a consumer adding translated tooltips to the tooltip list
 */
class WirelessTransferCoverTooltipBehavior(private val tooltips: Consumer<MutableList<Component>>) : IAddInformation {
	override fun appendHoverText(stack: ItemStack, level: Level?, tooltipComponents: MutableList<Component>, isAdvanced: TooltipFlag) {
		val tag = stack.tag
		if (tag != null) {
			val itemId = tag.getString("blockid")
			val pos = tag.getString("pos")
			if (!itemId.isEmpty() && !pos.isEmpty()) {
				val lst: MutableList<Component> = ArrayList()
				lst.add(
					Component.translatable(
						"item.gtmoremachine.wireless_transfer.tooltip.1",
						Component.translatable(itemId),
						pos,
					),
				)
				tooltipComponents.addAll(lst)
			}
		}
		tooltips.accept(tooltipComponents)
	}
}
