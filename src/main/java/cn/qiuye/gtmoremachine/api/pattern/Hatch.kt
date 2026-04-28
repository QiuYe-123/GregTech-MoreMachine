package cn.qiuye.gtmoremachine.api.pattern

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility

import net.minecraft.world.level.block.Block

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

import java.lang.reflect.Modifier

object Hatch {

	@JvmStatic
	val BlockSet: ObjectOpenHashSet<Block> by lazy {
		ObjectOpenHashSet<Block>().apply {
			for (field in PartAbility::class.java.declaredFields) {
				if (!Modifier.isStatic(field.modifiers) || field.type != PartAbility::class.java) {
					continue
				}
				val ability = field.get(null) as? PartAbility ?: continue
				addAll(ability.allBlocks)
			}
		}
	}
}
