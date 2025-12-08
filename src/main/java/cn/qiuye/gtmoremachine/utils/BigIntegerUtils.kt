package cn.qiuye.gtmoremachine.utils

import java.math.BigDecimal
import java.math.BigInteger

object BigIntegerUtils {

    @JvmField
    val big_integer_max_kong: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)

    @JvmField
    val big_integer_max_int: BigInteger = BigInteger.valueOf(Int.MAX_VALUE.toLong())

    @JvmStatic
    fun getLongValue(bigInt: BigInteger): Long {
        if (bigInt > big_integer_max_kong) {
            return Long.MAX_VALUE
        }
        return bigInt.toLong()
    }

    @JvmStatic
    fun getIntValue(bigInt: BigInteger): Int {
        if (bigInt > big_integer_max_int) {
            return Int.MAX_VALUE
        }
        return bigInt.toInt()
    }

    @JvmStatic
    fun getStringValue(bigInt: BigInteger): String {
        if (bigInt > BigInteger.ZERO) {
            return bigInt.toString(16)
        }
        return BigInteger.ZERO.toString()
    }

    @JvmStatic
    fun setBigIntegerValue(defaultValue: String): BigInteger = BigInteger(defaultValue, 16)

    @JvmStatic
    fun setBigDecimalValue(defaultValue: String): BigDecimal = BigDecimal(BigInteger(defaultValue, 16))
}
