package cn.qiuye.gtmoremachine.utils

import cn.qiuye.gtmoremachine.api.gui.monitor.Format

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

    @JvmStatic
    fun formatBigDecimalNumberOrSic(number: BigDecimal, format: Format): String = if (format ==
        Format.Unit
    ) {
        formatBigDecimalNumberOrSic(number)
    } else {
        FormattingUtil.DECIMAL_FORMAT_SIC_2F.format(number)
    }

    @JvmStatic
    fun formatBigIntegerNumberOrSic(number: BigInteger, format: Format): String = if (format ==
        Format.Unit
    ) {
        formatBigIntegerNumberOrSic(number)
    } else {
        FormattingUtil.DECIMAL_FORMAT_SIC_2F.format(BigDecimal(number))
    }
}
