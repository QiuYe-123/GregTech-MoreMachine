package cn.qiuye.gtmoremachine.api.item

import cn.qiuye.gtmoremachine.config.GTMMConfig

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

import it.unimi.dsi.fastutil.ints.IntIntPair

operator fun IntIntPair.component1(): Int = this.firstInt()
operator fun IntIntPair.component2(): Int = this.secondInt()

@OnlyIn(Dist.CLIENT)
class ModularHUD {
    private var stringAmount: Byte = 0
    private var stringWidth: Int = 0
    private val stringList = ArrayList<Component>()

    fun newString(string: Component) {
        this.stringAmount++
        this.stringList.add(string)
        val stringWidth = mc.font.width(string)
        if (stringWidth > this.stringWidth) {
            this.stringWidth = stringWidth
        }
    }

    fun draw(poseStack: GuiGraphics) {
        repeat(stringAmount.toInt()) { i ->
            val (posX, posY) = getStringCoord(i)
            poseStack.drawString(
                mc.font,
                stringList[i],
                posX,
                posY,
                0xFFFFFF,
                false,
            )
        }
    }

    private fun getStringCoord(index: Int): IntIntPair {
        val hudOffsetX = 0
        val hudOffsetY = 0
        val fontHeight = mc.font.lineHeight
        val windowHeight = mc.window.guiScaledHeight
        val windowWidth = mc.window.guiScaledWidth
        val stringWidth = this.stringWidth
        return when (GTMMConfig.INSTANCE.wirelessAlign) {
            1 -> {
                val posX = 1 + hudOffsetX
                val posY = 1 + hudOffsetY + (fontHeight * index)
                IntIntPair.of(posX, posY)
            }

            2 -> {
                val posX = windowWidth - (1 + hudOffsetX) - stringWidth
                val posY = 1 + hudOffsetY + (fontHeight * index)
                IntIntPair.of(posX, posY)
            }

            3 -> {
                val posX = 1 + hudOffsetX
                val posY = windowHeight - fontHeight * (stringAmount - index) - 1 -
                    hudOffsetY
                IntIntPair.of(posX, posY)
            }

            4 -> {
                val posX = windowWidth - (1 + hudOffsetX) - stringWidth
                val posY = windowHeight - fontHeight * (stringAmount - index) - 1 -
                    hudOffsetY
                IntIntPair.of(posX, posY)
            }

            else -> throw IllegalArgumentException(
                "Armor Hud config hudLocation is improperly configured. Allowed values: [1,2,3,4]",
            )
        }
    }

    fun reset() {
        this.stringAmount = 0
        this.stringList.clear()
    }

    companion object {
        private val mc: Minecraft = Minecraft.getInstance()
    }
}
