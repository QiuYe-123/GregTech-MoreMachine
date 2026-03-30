package cn.qiuye.gtmoremachine.api.pattern

import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine
import com.gregtechceu.gtceu.api.registry.GTRegistries

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

object Hatch {

	@JvmStatic
	val BlockSet: Set<Block> = ObjectOpenHashSet<Block>().apply {
		GTRegistries.MACHINES.forEach { d ->
			val block = d.block
			val machine = d.blockEntityType.create(BlockPos.ZERO, block.defaultBlockState())
			if (machine is MultiblockPartMachine) {
				add(block)
			}
		}
	}
}
