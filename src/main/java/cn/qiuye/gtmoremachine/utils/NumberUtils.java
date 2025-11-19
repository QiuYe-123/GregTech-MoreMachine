package cn.qiuye.gtmoremachine.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigDecimal;
import java.math.BigInteger;

import static cn.qiuye.gtmoremachine.utils.FormattingUtil.formatNumber;

public class NumberUtils {

    public static String formatLong(long number) {
        return FormattingUtil.formatNumber(number);
    }

    public static MutableComponent LongnumberText(long number) {
        return Component.literal(formatLong(number));
    }

    public static String formatDouble(double number) {
        return FormattingUtil.formatNumber(number);
    }

    public static MutableComponent DoublenumberText(double number) {
        return Component.literal(formatDouble(number));
    }

    public static String formatBigDecimalNumberOrSic(BigDecimal number) {
        return FormattingUtil.formatNumberReadable(number);
    }

    public static String formatBigIntegerNumberOrSic(BigInteger number) {
        return FormattingUtil.formatNumberReadable(new BigDecimal(number));
    }
}
