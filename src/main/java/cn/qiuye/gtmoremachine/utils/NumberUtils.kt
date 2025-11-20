package cn.qiuye.gtmoremachine.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

import java.math.BigDecimal
import java.math.BigInteger

object NumberUtils {

    @JvmStatic
    fun formatLong(number: Long): String = FormattingUtil.formatNumber(number.toDouble())

    @JvmStatic
    fun longnumberText(number: Long): MutableComponent = Component.literal(formatLong(number))

    @JvmStatic
    fun formatDouble(number: Double): String = FormattingUtil.formatNumber(number)

    @JvmStatic
    fun doublenumberText(number: Double): MutableComponent = Component.literal(formatDouble(number))

    @JvmStatic
    fun formatBigDecimalNumberOrSic(number: BigDecimal): String = FormattingUtil.formatNumberReadable(number)

    @JvmStatic
    fun formatBigIntegerNumberOrSic(number: BigInteger): String =
        FormattingUtil.formatNumberReadable(BigDecimal(number))
}
