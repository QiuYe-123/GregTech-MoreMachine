package cn.qiuye.gtmoremachine.utils.nbt

import cn.qiuye.gtmoremachine.api.gui.monitor.DefaultValueProvider

import net.minecraft.world.item.ItemStack

import java.util.UUID

object TagUtils {

	@JvmStatic
	fun <T : Enum<T>> getEnumTag(tagKey: String, stack: ItemStack, enumClass: Class<T>, defaultValueProvider: DefaultValueProvider<T>): T {
		val tag = stack.orCreateTag
		return if (!tag.isEmpty && tag.contains(tagKey)) {
			java.lang.Enum.valueOf(enumClass, tag.getString(tagKey))
		} else {
			defaultValueProvider.defaultValue
		}
	}

	@JvmStatic
	fun <T : Enum<*>> setEnumTag(tagKey: String, tagValue: T, stack: ItemStack) {
		setStringTag(tagKey, tagValue.toString(), stack)
	}

	/**
	 * 设置统计模式
	 *
	 * @param tagKey Tag的key名
	 * @param tag tag
	 * @param stack 物品堆
	 */
	@JvmStatic
	fun setStringTag(tagKey: String, tag: String, stack: ItemStack) {
		val itemStacktag = stack.getOrCreateTag()
		itemStacktag.putString(tagKey, tag)
		stack.tag = itemStacktag
	}

	/**
	 * 从物品堆获取对应Tagkey是否存在
	 *
	 * @param tagkey Tag的key名
	 * @param itemStack 物品堆
	 * @return Boolean 物品堆是否存在Tagkey对应的值
	 */
	@JvmStatic
	fun hasTagKey(tagkey: String, itemStack: ItemStack): Boolean {
		val tag = itemStack.getOrCreateTag()
		return tag.contains(tagkey)
	}

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
}
