package cn.qiuye.gtmoremachine.utils.datagen

import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap

import java.util.ResourceBundle

object ChineseConverter {
    private val mappingTable: MutableMap<Char, Char> = Char2CharOpenHashMap()

    fun convert(cn: String): String {
        val cntw = StringBuilder()

        for (cnstr in cn.toCharArray()) {
            val cntwstr = mappingTable[cnstr]
            if (cntwstr == null) {
                cntw.append(cnstr)
            } else {
                cntw.append(cntwstr)
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
