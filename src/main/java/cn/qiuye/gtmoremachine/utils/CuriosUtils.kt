package cn.qiuye.gtmoremachine.utils

import cn.qiuye.gtmoremachine.GTmm

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

import top.theillusivec4.curios.api.CuriosApi

object CuriosUtils {
    fun getItemCuriosEquipped(player: Player, item: Item): ItemStack {
        if (GTmm.Mods.isCuriosLoaded()) {
            val curiosInventory = CuriosApi.getCuriosInventory(player)

            if (curiosInventory.isPresent && curiosInventory.resolve().isPresent) {
                return curiosInventory.resolve().get().findFirstCurio(item).get().stack
            }
        }
        return ItemStack.EMPTY
    }

    fun getItemInventoryEquipped(player: Player, item: Item): ItemStack =
        player.inventory.items.find { itemStack -> itemStack.item == item } ?: ItemStack.EMPTY

    fun getItemEquipped(player: Player, item: Item): ItemStack {
        var stack = getItemInventoryEquipped(player, item)
        if (stack.isEmpty)stack = getItemCuriosEquipped(player, item)
        return stack
    }
}
