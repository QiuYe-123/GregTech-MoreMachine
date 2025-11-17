package cn.qiuye.gtmoremachine.utils;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NumberUtils {

    public static String formatNumber(double number) {
        final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D" };
        DecimalFormat df = new DecimalFormat("#.##");
        double temp = number;
        int unitIndex = 0;
        while (temp >= 1000 && unitIndex < UNITS.length - 1) {
            temp /= 1000;
            unitIndex++;
        }
        return df.format(temp) + UNITS[unitIndex];
    }

    public static String formatLong(long number) {
        return formatNumber(number);
    }

    public static String formatDouble(double number) {
        return formatNumber(number);
    }

    public static MutableComponent DoublenumberText(double number) {
        return Component.literal(formatDouble(number));
    }

    public static MutableComponent LongnumberText(long number) {
        return Component.literal(formatLong(number));
    }

    public static String formatBigDecimalNumberOrSic(BigDecimal number) {
        return FormattingUtil.formatNumberReadable(number);
    }

    public static String formatBigIntegerNumberOrSic(BigInteger number) {
        return FormattingUtil.formatNumberReadable(new BigDecimal(number));
    }
}
