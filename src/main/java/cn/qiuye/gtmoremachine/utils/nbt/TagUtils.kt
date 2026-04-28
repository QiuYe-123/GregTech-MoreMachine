package cn.qiuye.gtmoremachine.utils.nbt

import cn.qiuye.gtmoremachine.api.gui.monitor.DefaultValueProvider

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

import java.util.UUID

object TagUtils {

	private fun getCustomDataTag(stack: ItemStack): CompoundTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()

	@JvmStatic
	fun <T : Enum<T>> getEnumTag(tagKey: String, stack: ItemStack, enumClass: Class<T>, defaultValueProvider: DefaultValueProvider<T>): T {
		val tag = getCustomDataTag(stack)
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
		CustomData.update(DataComponents.CUSTOM_DATA, stack) {
			it.putString(tagKey, tag)
		}
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
		val tag = getCustomDataTag(itemStack)
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
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack) {
			it.putUUID("UUID", uuid)
		}
	}

	/**
	 * 从物品堆获取UUID
	 *
	 * @param itemStack 物品堆
	 * @return UUID
	 */
	@JvmStatic
	fun getUUID(itemStack: ItemStack): UUID? {
		val tag = getCustomDataTag(itemStack)
		return if (!tag.isEmpty && tag.contains("UUID")) {
			tag.getUUID("UUID")
		} else {
			null
		}
	}
}
