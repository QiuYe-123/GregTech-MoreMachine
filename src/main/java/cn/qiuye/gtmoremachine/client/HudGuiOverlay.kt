package cn.qiuye.gtmoremachine.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay

import lombok.NoArgsConstructor

@NoArgsConstructor
class HudGuiOverlay : IGuiOverlay {
    override fun render(
        forgeGui: ForgeGui,
        guiGraphics: GuiGraphics,
        partialTick: Float,
        screenWidth: Int,
        screenHeight: Int,
    ) {
        val mc = Minecraft.getInstance()
        if (mc.isWindowActive && mc.level != null && !mc.options.renderDebug && !mc.options.hideGui) {
        }
    }

    companion object {
    }
}
