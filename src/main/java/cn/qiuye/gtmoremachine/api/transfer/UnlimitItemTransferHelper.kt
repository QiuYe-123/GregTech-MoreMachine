package cn.qiuye.gtmoremachine.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

import java.util.function.Predicate
import kotlin.math.min

object UnlimitItemTransferHelper {

	@JvmStatic
	fun exportToTarget(source: IItemHandler, maxAmount: Int, predicate: Predicate<ItemStack>, level: Level, pos: BlockPos, direction: Direction) {
		var maxAmount = maxAmount
		if (level.getBlockState(pos).hasBlockEntity()) {
			val blockEntity = level.getBlockEntity(pos)
			if (blockEntity != null) {
				val target = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction)
				if (target != null) {
					for (srcIndex in 0..<source.slots) {
						while (true) {
							var sourceStack = source.extractItem(srcIndex, Int.MAX_VALUE, true)
							if (sourceStack.isEmpty || !predicate.test(sourceStack)) {
								break
							}
							val remainder = insertItem(target, sourceStack, true)
							val amountToInsert = sourceStack.count - remainder.count
							if (amountToInsert > 0) {
								sourceStack = source.extractItem(srcIndex, min(maxAmount, amountToInsert), false)
								insertItem(target, sourceStack, false)
								maxAmount -= min(maxAmount, amountToInsert)
								if (maxAmount <= 0) return
							} else {
								break
							}
						}
					}
				}
			}
		}
	}

	fun insertItem(handler: IItemHandler?, stack: ItemStack, simulate: Boolean): ItemStack {
		var stack = stack
		if (handler == null || stack.isEmpty) {
			return stack
		}
		if (!stack.isStackable) {
			return insertToEmpty(handler, stack, simulate)
		}

		val emptySlots: IntList = IntArrayList()
		val slots = handler.slots

		for (i in 0..<slots) {
			val slotStack = handler.getStackInSlot(i)
			if (slotStack.isEmpty) {
				emptySlots.add(i)
			}
			if (ItemStack.isSameItemSameComponents(stack, slotStack)) {
				stack = handler.insertItem(i, stack, simulate)
				if (stack.isEmpty) {
					return ItemStack.EMPTY
				}
			}
		}

		val iterator = emptySlots.intIterator()
		while (iterator.hasNext()) {
			val slot = iterator.nextInt()
			stack = handler.insertItem(slot, stack, simulate)
			if (stack.isEmpty) {
				return ItemStack.EMPTY
			}
		}
		return stack
	}

	private fun insertToEmpty(handler: IItemHandler, stack: ItemStack, simulate: Boolean): ItemStack {
		var remainder = stack
		for (i in 0..<handler.slots) {
			if (handler.getStackInSlot(i).isEmpty) {
				remainder = handler.insertItem(i, remainder, simulate)
				if (remainder.isEmpty) {
					return ItemStack.EMPTY
				}
			}
		}
		return remainder
	}
}
