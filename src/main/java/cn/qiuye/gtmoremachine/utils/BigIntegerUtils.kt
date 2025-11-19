package cn.qiuye.gtmoremachine.utils

import java.math.BigInteger

object BigIntegerUtils {

    @JvmStatic
    val BIG_INTEGER_MAX_LONG: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)

    fun getLongValue(bigInt: BigInteger): Long {
        if (bigInt > BIG_INTEGER_MAX_LONG) {
            return Long.MAX_VALUE
        }
        return bigInt.toLong()
    }
}
