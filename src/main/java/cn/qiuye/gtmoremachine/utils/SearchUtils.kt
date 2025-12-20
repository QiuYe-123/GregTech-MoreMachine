package cn.qiuye.gtmoremachine.utils

import cn.qiuye.gtmoremachine.GTmm

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

import top.theillusivec4.curios.api.CuriosApi

object SearchUtils {
    fun getItemCuriosEquipped(player: Player, item: Item): ItemStack {
        if (!GTmm.Mods.isCuriosLoaded()) {
            return ItemStack.EMPTY
        }

        return CuriosApi.getCuriosInventory(player)
            .takeIf { it.isPresent }
            ?.resolve()
            ?.takeIf { it.isPresent }
            ?.get()
            ?.findFirstCurio(item)
            ?.takeIf { it.isPresent }
            ?.get()
            ?.stack
            ?: ItemStack.EMPTY
    }

    fun getItemInventoryEquipped(player: Player, item: Item): ItemStack =
        player.inventory.items.find { itemStack -> itemStack.item == item } ?: ItemStack.EMPTY

    fun getItemEquipped(player: Player, item: Item): ItemStack {
        var stack = getItemCuriosEquipped(player, item)
        if (stack.isEmpty) stack = getItemInventoryEquipped(player, item)
        return stack
    }
}
