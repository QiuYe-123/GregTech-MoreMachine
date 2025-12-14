package cn.qiuye.gtmoremachine.client

import cn.qiuye.gtmoremachine.common.data.GTMMItems
import cn.qiuye.gtmoremachine.utils.CuriosUtils.getItemEquipped

import com.gregtechceu.gtceu.api.item.ComponentItem
import com.gregtechceu.gtceu.api.item.component.IItemHUDProvider

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay

import lombok.NoArgsConstructor

@NoArgsConstructor
class GTmmHudGuiOverlay : IGuiOverlay {
    override fun render(
        forgeGui: ForgeGui,
        guiGraphics: GuiGraphics,
        partialTick: Float,
        screenWidth: Int,
        screenHeight: Int,
    ) {
        val mc = Minecraft.getInstance()
        if (mc.level != null && !mc.options.renderDebug && !mc.options.hideGui) {
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
