package cn.qiuye.gtmoremachine.client

import cn.qiuye.gtmoremachine.common.data.GTMMItems
import cn.qiuye.gtmoremachine.utils.SearchUtils.getItemEquipped

import com.gregtechceu.gtceu.api.item.ComponentItem
import com.gregtechceu.gtceu.api.item.component.IItemHUDProvider

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.world.item.ItemStack

import lombok.NoArgsConstructor

@NoArgsConstructor
class HudGuiOverlay : LayeredDraw.Layer {
	override fun render(guiGraphics: GuiGraphics, tracker: DeltaTracker) {
		val mc = Minecraft.getInstance()
		if (mc.isWindowActive && mc.level != null && !mc.gui.debugOverlay.showDebugScreen() && !mc.options.hideGui) {
			renderHUDMetaItem(getItemEquipped(mc.player!!, GTMMItems.WIRELESS_ENERGY_TERMINAL.asItem()), guiGraphics)
		}
	}

	companion object {

		private fun renderHUDMetaItem(stack: ItemStack, guiGraphics: GuiGraphics?) {
			if (stack.item is ComponentItem) {
				val componentItem = stack.item as ComponentItem
				for (behaviour in componentItem.getComponents()) {
					if (behaviour is IItemHUDProvider) {
						IItemHUDProvider.tryDrawHud(behaviour, stack, guiGraphics)
					}
				}
			}
		}
	}
}
