package cn.qiuye.gtmoremachine.api.pattern

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine
import com.gregtechceu.gtceu.api.registry.GTRegistries

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

object Hatch {

	@JvmStatic
	val BlockSet: ObjectOpenHashSet<Block> by lazy {
		ObjectOpenHashSet<Block>().apply {
			GTRegistries.MACHINES.forEach { d ->
				if (d is MultiblockMachineDefinition) {
					val block = d.get()
					val machine = BlockEntity.loadStatic(BlockPos.ZERO, block.defaultBlockState(), CompoundTag())
// 					val machine = d.blockEntityType.create(BlockPos.ZERO, block.defaultBlockState())
					if (machine is MultiblockPartMachine) {
						add(block)
					}
				}
			}
		}
	}
}
