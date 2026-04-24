package cn.qiuye.gtmoremachine.api.pattern

import com.gregtechceu.gtceu.api.machine.MachineDefinition
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
			for (d in GTRegistries.MACHINES) {
				// 排除多方块控制器定义，且无配方类型的机器视为可能的部件
				if (d !is MultiblockMachineDefinition) {
					val block = d.get()
					try {
						// 使用静态方法加载方块实体，避免依赖 MachineDefinition 的内部创建器
						val machine = BlockEntity.loadStatic(
							BlockPos.ZERO,
							block.defaultBlockState(),
							CompoundTag(),
						)
						if (machine is MultiblockPartMachine) {
							add(block)
						}
					} catch (_: Exception) {
						// 忽略加载异常
					}
				}
			}
		}
	}
}
