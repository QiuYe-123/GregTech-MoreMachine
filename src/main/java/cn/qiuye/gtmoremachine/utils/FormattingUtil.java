package cn.qiuye.gtmoremachine.utils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FormattingUtil {

    public static final DecimalFormat DECIMAL_FORMAT_1F = new DecimalFormat("#,##0.#");
    public static final DecimalFormat DECIMAL_FORMAT_2F = new DecimalFormat("#,##0.##");
    public static final DecimalFormat DECIMAL_FORMAT_SIC = new DecimalFormat("0E0");
    public static final DecimalFormat DECIMAL_FORMAT_SIC_2F = new DecimalFormat("0.00E00");

    /**
     * Check if {@code string} has any uppercase characters.
     *
     * @param string the string to check
     * @return if the string has any uppercase characters.
     */
    public static boolean hasUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (Character.isUpperCase(ch)) return true;
        }
        return false;
    }

    /**
     * Does almost the same thing as {@code UPPER_CAMEL.to(LOWER_UNDERSCORE, string)},
     * but it also inserts underscores between words and numbers.
     *
     * @param string Any string with ASCII characters.
     * @return A string that is all lowercase, with underscores inserted before word/number boundaries:
     *
     *         <pre>
     *         <br>{@code "maragingSteel300" -> "maraging_steel_300"}
     *         <br>{@code "gtceu:maraging_steel_300" -> "gtceu:maraging_steel_300"}
     *         <br>{@code "maragingSteel_300" -> "maraging_steel_300"}
     *         <br>{@code "maragingSTEEL_300" -> "maraging_steel_300"}
     *         <br>{@code "MARAGING_STEEL_300" -> "maraging_steel_300"}
     * </pre>
     */
    public static String toLowerCaseUnderscore(String string) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char curChar = string.charAt(i);
            result.append(Character.toLowerCase(curChar));
            if (i == string.length() - 1) break;

            char nextChar = string.charAt(i + 1);
            if (curChar == '_' || nextChar == '_') continue;
            boolean nextIsUpper = Character.isUpperCase(nextChar);
            if (Character.isUpperCase(curChar) && nextIsUpper) continue;
            if (nextIsUpper || Character.isDigit(curChar) ^ Character.isDigit(nextChar)) result.append('_');
        }
        return result.toString();
    }

    public static MutableComponent formatWithConstantWidth(String labelKey, Component body, Component... appends) {
        var a = new Component[appends.length + 1];
        a[0] = body;
        int i = 0;
        for (var c : appends) {
            a[++i] = c;
        }
        var spacer = ".";
        var spacerComponent = Component.literal(spacer);
        a[0] = spacerComponent.append(body);
        return Component.translatable(labelKey, (Object[]) a);
    }

    public static Component voltageName(BigDecimal avgEnergy) {
        return Component.literal(GTValues.VNF[GTUtil.getFloorTierByVoltage(avgEnergy.min(BigDecimal.valueOf(Long.MAX_VALUE)).abs().longValue())]);
    }

    public static BigDecimal voltageAmperage(BigDecimal avgEnergy) {
        return avgEnergy.abs().divide(BigDecimal.valueOf(GTValues.VEX[GTUtil.getFloorTierByVoltage(avgEnergy.min(BigDecimal.valueOf(Long.MAX_VALUE)).abs().longValue())]), 1, RoundingMode.FLOOR);
    }

    public static String getSpacer(Font font, String splitChar, int spaceLength) {
        int spacerCount = spaceLength / font.width(splitChar);
        while (font.width(splitChar.repeat(spacerCount) + " ") <= spaceLength) {
            spacerCount++;
        }
        return splitChar.repeat(spacerCount - 2) + " ";
    }

    private static String stripColor(String text) {
        return Minecraft.getInstance().options.chatColors().get() ? text : ChatFormatting.stripFormatting(text);
    }

    public static List<FormattedCharSequence> formatJustifyComponent(FormattedText component, int maxWidth, Font font, String splitChar) {
        ComponentCollector componentcollector = new ComponentCollector();
        AtomicInteger before = new AtomicInteger();
        AtomicInteger after = new AtomicInteger();
        AtomicBoolean hasSplit = new AtomicBoolean(false);
        component.visit((style, text) -> {
            if (text.equals(splitChar)) {
                hasSplit.set(true);
            } else {
                int width = font.width(text);
                (hasSplit.get() ? after : before).getAndAdd(width);
            }
            return Optional.empty();
        }, Style.EMPTY);
        component.visit((style, text) -> {
            String content = text.equals(splitChar) ? getSpacer(font, splitChar, maxWidth - before.get() - after.get()) : text;
            componentcollector.append(FormattedText.of(stripColor(content), style));
            return Optional.empty();
        }, Style.EMPTY);
        List<FormattedCharSequence> list = Lists.newArrayList();
        font.getSplitter().splitLines(componentcollector.getResultOrEmpty(), maxWidth, Style.EMPTY, (text, p_94004_) -> {
            FormattedCharSequence formattedcharsequence = Language.getInstance().getVisualOrder(text);
            list.add(p_94004_ ? FormattedCharSequence.composite(FormattedCharSequence.codepoint(32, Style.EMPTY), formattedcharsequence) : formattedcharsequence);
        });
        return (list.isEmpty() ? Lists.newArrayList(FormattedCharSequence.EMPTY) : list);
    }

    private static final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D", "C", "S", "O", "Q", "X", "W", "V", "U", "Tt", "Gt", "Mt", "St", "Ot", "Nt", "Dt", "Ct", "Lt", "Kt", "Jt", "It", "Ht", "Gtt", "Ett", "Dtt", "Ctt", "Btt", "Att" };

    private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);

    public static String formatNumberReadable(BigDecimal number) {
        return formatNumberReadable(number, false);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli) {
        return formatNumberReadable(number, milli, DECIMAL_FORMAT_1F, null);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli, NumberFormat fmt, @Nullable String unit) {
        StringBuilder sb = new StringBuilder();
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal number1 = number;
        if (number.compareTo(zero) < 0) {
            number = number.abs();
            sb.append('-');
        }

        if (milli && number.compareTo(ONE_THOUSAND) >= 0) {
            milli = false;
            number = number.divide(ONE_THOUSAND, MathContext.DECIMAL128);
        }

        int exp = 0;
        if (number.compareTo(ONE_THOUSAND) >= 0) {
            exp = calculateExponent(number);

            // 当指数超过单位数组范围时使用科学计数法
            if (exp > UNITS.length - 1) {
                return DECIMAL_FORMAT_SIC_2F.format(number);
            }

            // 使用BigDecimal进行幂运算
            if (exp > 0) {
                BigDecimal divisor = power(exp);
                number1 = number.divide(divisor, MathContext.DECIMAL128);
            }
        }

        sb.append(fmt.format(number1));
        if (exp > 0) {
            sb.append(UNITS[exp]);
        } else if (milli && number1.compareTo(zero) != 0) {
            sb.append('m');
        }

        if (unit != null) sb.append(unit);
        return sb.toString();
    }

    /**
     * 使用BigDecimal计算指数，避免double精度限制
     */
    private static int calculateExponent(BigDecimal number) {
        int exponent = 0;
        BigDecimal temp = number;

        // 循环除以1000，直到数值小于1000
        while (temp.compareTo(ONE_THOUSAND) >= 0) {
            temp = temp.divide(ONE_THOUSAND, MathContext.DECIMAL128);
            exponent++;
        }

        return exponent;
    }

    /**
     * 使用BigDecimal计算幂运算
     */
    private static BigDecimal power(int exponent) {
        if (exponent == 0) return BigDecimal.ONE;

        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < exponent; i++) {
            result = result.multiply(ONE_THOUSAND);
        }
        return result;
    }

    public static String formatNumber(double number) {
        final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D" };
        DecimalFormat df = new DecimalFormat("#.##");
        double temp = number;
        int unitIndex = 0;
        while (temp >= 100 && unitIndex < UNITS.length - 1) {
            temp /= 100;
            unitIndex++;
        }
        return df.format(temp) + UNITS[unitIndex];
    }
}
