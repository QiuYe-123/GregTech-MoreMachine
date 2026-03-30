package cn.qiuye.gtmoremachine.utils.datagen

import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap

import java.util.ResourceBundle

object ChineseConverter {
	private val mappingTable = Char2CharOpenHashMap()

	@JvmStatic
	fun convert(cn: String): String {
		val cntw = StringBuilder()

		for (cnstr in cn.toCharArray()) {
			if (mappingTable.containsKey(cnstr)) {
				val cntwstr = mappingTable[cnstr]
				cntw.append(cntwstr)
			} else {
				cntw.append(cnstr)
			}
		}

		return cntw.toString()
	}

	init {
		val properties = ResourceBundle.getBundle("SimplifiedToTraditional")

		for (runtime in properties.keySet()) {
			mappingTable[runtime[0]] = properties.getString(runtime)[0]
		}
	}
}
