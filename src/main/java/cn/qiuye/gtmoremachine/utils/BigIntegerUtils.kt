package cn.qiuye.gtmoremachine.utils

import java.math.BigInteger

object BigIntegerUtils {

    @JvmField
    val big_integer_max_kong: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)

    @JvmStatic
    fun getLongValue(bigInt: BigInteger): Long {
        if (bigInt > big_integer_max_kong) {
            return Long.MAX_VALUE
        }
        return bigInt.toLong()
    }
}
