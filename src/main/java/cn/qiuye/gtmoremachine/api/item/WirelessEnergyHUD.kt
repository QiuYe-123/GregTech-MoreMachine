package cn.qiuye.gtmoremachine.api.item

import cn.qiuye.gtmoremachine.config.GTMMConfig

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

import it.unimi.dsi.fastutil.ints.IntIntPair

import javax.annotation.Nonnull

@OnlyIn(Dist.CLIENT)
class WirelessEnergyHUD {
    private var stringAmount: Byte = 0
    private val stringList = ArrayList<Component>()

    fun newString(string: Component) {
        this.stringAmount++
        this.stringList.add(string)
    }

    fun draw(poseStack: GuiGraphics) {
        for (i in 0..stringAmount) {
            val coords = this.getStringCoord(i)
            poseStack.drawString(
                mc.font,
                stringList[i],
                coords.firstInt(),
                coords.secondInt(),
                0xFFFFFF,
                false,
            )
        }
    }

    @Nonnull
    private fun getStringCoord(index: Int): IntIntPair {
        val posX: Int
        val posY: Int
        val hudOffsetX = 0
        val hudOffsetY = 0
        val fontHeight = mc.font.lineHeight
        val windowHeight = mc.window.guiScaledHeight
        val windowWidth = mc.window.guiScaledWidth
        val stringWidth = mc.font.width(stringList[index])
        when (GTMMConfig.INSTANCE.wirelessAlign) {
            1 -> {
                posX = 1 + hudOffsetX
                posY = 1 + hudOffsetY + (fontHeight * index)
            }

            2 -> {
                posX = windowWidth - (1 + hudOffsetX) - stringWidth
                posY = 1 + hudOffsetY + (fontHeight * index)
            }

            3 -> {
                posX = 1 + hudOffsetX
                posY = windowHeight - fontHeight * (stringAmount - index) - 1 -
                    hudOffsetY
            }

            4 -> {
                posX = windowWidth - (1 + hudOffsetX) - stringWidth
                posY = windowHeight - fontHeight * (stringAmount - index) - 1 -
                    hudOffsetY
            }

            else -> throw IllegalArgumentException(
                "Armor Hud config hudLocation is improperly configured. Allowed values: [1,2,3,4]",
            )
        }
        return IntIntPair.of(posX, posY)
    }

    fun reset() {
        this.stringAmount = 0
        this.stringList.clear()
    }

    companion object {
        private val mc: Minecraft = Minecraft.getInstance()
    }
}
