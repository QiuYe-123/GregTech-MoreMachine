package cn.qiuye.gtmoremachine.utils

import net.minecraft.world.item.ItemStack
import java.util.*

object TagUtils {

    // ==================== UUID 相关 ====================

    /**
     * 设置UUID到物品堆
     *
     * @param uuid      UUID
     * @param itemStack 物品堆
     */
    @JvmStatic
    fun setUUID(uuid: UUID, itemStack: ItemStack) {
        val tag = itemStack.getOrCreateTag()
        tag.putUUID("UUID", uuid)
        itemStack.tag = tag
    }

    /**
     * 从物品堆获取UUID
     *
     * @param itemStack 物品堆
     * @return UUID
     */
    @JvmStatic
    fun getUUID(itemStack: ItemStack): UUID? {
        val tag = itemStack.getOrCreateTag()
        return if (!tag.isEmpty && tag.contains("UUID")) {
            tag.getUUID("UUID")
        } else {
            null
        }
    }

    /**
     * 从物品堆获取对应Tagkey是否存在
     *
     * @param itemStack 物品堆
     * @param tagkey Tag的key名
     * @return boolean
     */
    @JvmStatic
    fun hasTagKey(itemStack: ItemStack, tagkey: String): Boolean {
        val tag = itemStack.getOrCreateTag()
        return tag.contains(tagkey)
    }
}