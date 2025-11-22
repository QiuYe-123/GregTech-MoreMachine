package cn.qiuye.gtmoremachine.utils

import net.minecraft.core.NonNullList
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

import appeng.api.config.Actionable
import appeng.api.networking.IGrid
import appeng.api.stacks.AEItemKey
import appeng.items.tools.powered.WirelessTerminalItem
import it.unimi.dsi.fastutil.ints.IntObjectPair

class GetMEStorage {

    companion object {
        fun getMEStorage(stack: ItemStack, player: Player, candidates: List<ItemStack>): IntObjectPair<IItemHandler?>? {
            if (stack.item is WirelessTerminalItem &&
                stack.hasTag() &&
                stack.tag!!
                    .contains("accessPoint", 10)
            ) {
                val terminalItem = stack.item as WirelessTerminalItem
                val grid: IGrid? = terminalItem.getLinkedGrid(stack, player.level(), player)
                if (grid != null) {
                    val storage = grid.storageService.inventory
                    for (candidate in candidates) {
                        if (storage.extract(AEItemKey.of(candidate), 1, Actionable.MODULATE, null) > 0) {
                            val stacks = NonNullList.withSize(1, candidate)
                            val handler1: IItemHandler = ItemStackHandler(stacks)
                            return IntObjectPair.of<IItemHandler?>(0, handler1)
                        }
                    }
                }
            }
            return null
        }
    }
}
