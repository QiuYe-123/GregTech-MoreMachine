package cn.qiuye.gtmoremachine.utils

import cn.qiuye.gtmoremachine.GTmm

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.ForgeRegistries

object GetRegistries {

    @JvmStatic
    fun getBlock(s: String): Block? {
        val b = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(s))
        if (b === Blocks.AIR) {
            GTmm.LOGGER.error("未找到ID为{}的方块", s)
            return Blocks.BARRIER
        }
        return b
    }
}
