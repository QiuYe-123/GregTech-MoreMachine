package cn.qiuye.gtmoremachine.utils

import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.utils.GTUtil

import net.minecraft.ChatFormatting
import net.minecraft.client.ComponentCollector
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence

import com.google.common.collect.Lists
import org.jetbrains.annotations.Nullable

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

object FormattingUtil {
	val DECIMAL_FORMAT_1F = DecimalFormat("#,##0.#")
	val DECIMAL_FORMAT_2F = DecimalFormat("#,##0.##")
	val DECIMAL_FORMAT_SIC = DecimalFormat("0E0")
	val DECIMAL_FORMAT_SIC_2F = DecimalFormat("0.00E00")
	private val DF_FORMAT_NUMBER = DecimalFormat("#.##")

	/**
	 * Check if `string` has any uppercase characters.
	 *
	 * @param string the string to check
	 * @return if the string has any uppercase characters.
	 */
	@JvmStatic
	fun hasUpperCase(string: String): Boolean {
		for (ch in string) {
			if (ch.isUpperCase()) return true
		}
		return false
	}

	/**
	 * Does almost the same thing as `UPPER_CAMEL.to(LOWER_UNDERSCORE, string)`,
	 * but it also inserts underscores between words and numbers.
	 *
	 * @param string Any string with ASCII characters.
	 * @return A string that is all lowercase, with underscores inserted before word/number boundaries:
	 *
	 * <pre>
	 * <br>`"maragingSteel300" -> "maraging_steel_300"`
	 * <br>`"gtceu:maraging_steel_300" -> "gtceu:maraging_steel_300"`
	 * <br>`"maragingSteel_300" -> "maraging_steel_300"`
	 * <br>`"maragingSTEEL_300" -> "maraging_steel_300"`
	 * <br>`"MARAGING_STEEL_300" -> "maraging_steel_300"`
	 * </pre>
	 */
	@JvmStatic
	fun toLowerCaseUnderscore(string: String): String {
		val result = StringBuilder()
		for (i in string.indices) {
			val curChar = string[i]
			result.append(curChar.lowercaseChar())
			if (i == string.length - 1) break
			val nextChar = string[i + 1]
			if (curChar == '_' || nextChar == '_') continue
			val nextIsUpper = nextChar.isUpperCase()
			if (curChar.isUpperCase() && nextIsUpper) continue
			if (nextIsUpper || curChar.isDigit() xor nextChar.isDigit()) result.append('_')
		}
		return result.toString()
	}

	@JvmStatic
	fun formatWithConstantWidth(labelKey: String, body: Component, vararg appends: Component): MutableComponent {
		val a = arrayOfNulls<Component>(appends.size + 1)
		a[0] = body
		var i = 0
		for (c in appends) {
			a[++i] = c
		}
		val spacer = "."
		val spacerComponent = Component.literal(spacer)
		a[0] = spacerComponent.append(body)
		return Component.translatable(labelKey, *a)
	}

	/** 预计算 BigDecimal.valueOf(Long.MAX_VALUE)，避免 voltageName/voltageAmperage 中重复分配 */
	private val LONG_MAX_BD = BigDecimal.valueOf(Long.MAX_VALUE)

	@JvmStatic
	fun voltageName(avgEnergy: BigDecimal): Component {
		val floorTier = GTUtil.getFloorTierByVoltage(
			avgEnergy.min(LONG_MAX_BD).abs().toLong(),
		).toInt()
		return Component.literal(GTValues.VNF[floorTier])
	}

	@JvmStatic
	fun voltageName(avgEnergy: Long): Component {
		val floorTier = GTUtil.getFloorTierByVoltage(
			abs(avgEnergy),
		).toInt()
		return Component.literal(GTValues.VNF[floorTier])
	}

	@JvmStatic
	fun voltageAmperage(avgEnergy: BigDecimal): BigDecimal {
		val floorTier = GTUtil.getFloorTierByVoltage(
			avgEnergy.min(LONG_MAX_BD).abs().toLong(),
		).toInt()
		return avgEnergy.abs().divide(
			BigDecimal.valueOf(GTValues.VEX[floorTier]),
			1,
			RoundingMode.FLOOR,
		)
	}

	@JvmStatic
	fun voltageAmperage(avgEnergy: Long): BigDecimal {
		val floorTier = GTUtil.getFloorTierByVoltage(
			abs(avgEnergy),
		).toInt()
		return BigDecimal.valueOf(abs(avgEnergy)).divide(
			BigDecimal.valueOf(GTValues.VEX[floorTier]),
			1,
			RoundingMode.FLOOR,
		)
	}

	fun getSpacer(font: Font, splitChar: String, spaceLength: Int): String {
		if (spaceLength <= 0) return " "
		val splitWidth = font.width(splitChar)
		if (splitWidth <= 0) return " "
		var spacerCount = spaceLength / splitWidth
		while (font.width(splitChar.repeat(spacerCount) + " ") <= spaceLength) {
			spacerCount++
		}
		return splitChar.repeat((spacerCount - 2).coerceAtLeast(0)) + " "
	}

	private fun stripColor(text: String): String = (
		if (Minecraft.getInstance().options.chatColors().get()) {
			text
		} else {
			ChatFormatting.stripFormatting(
				text,
			)
		}
		).toString()

	@JvmStatic
	fun formatJustifyComponent(component: FormattedText, maxWidth: Int, font: Font, splitChar: String): List<FormattedCharSequence> {
		val componentcollector = ComponentCollector()
		val before = AtomicInteger()
		val after = AtomicInteger()
		val hasSplit = AtomicBoolean(false)
		component.visit({ _, text ->
			if (text == splitChar) {
				hasSplit.set(true)
			} else {
				val width = font.width(text)
				(if (hasSplit.get()) after else before).getAndAdd(width)
			}
			Optional.empty<Any>()
		}, Style.EMPTY)
		component.visit({ style, text ->
			val content = if (text == splitChar) {
				getSpacer(
					font,
					splitChar,
					(maxWidth - before.get() - after.get()).coerceAtLeast(0),
				)
			} else {
				text
			}
			componentcollector.append(FormattedText.of(stripColor(content), style))
			Optional.empty<Any>()
		}, Style.EMPTY)
		val list = Lists.newArrayList<FormattedCharSequence>()
		font.splitter.splitLines(
			componentcollector.resultOrEmpty,
			maxWidth,
			Style.EMPTY,
		) { text, bool ->
			val formattedcharsequence = Language.getInstance().getVisualOrder(text)
			list.add(
				if (bool) {
					FormattedCharSequence.composite(
						FormattedCharSequence.codepoint(32, Style.EMPTY),
						formattedcharsequence,
					)
				} else {
					formattedcharsequence
				},
			)
		}
		return list.ifEmpty { Lists.newArrayList(FormattedCharSequence.EMPTY) }
	}

	private val UNITS = arrayOf(
		"", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D", "C", "S", "O", "Q", "X", "W", "V", "U", "Tt", "Gt",
		"Mt", "St", "Ot", "Nt", "Dt", "Ct", "Lt", "Kt", "Jt", "It", "Ht", "Gtt", "Ett", "Dtt", "Ctt", "Btt", "Att",
	)

	private val ONE_THOUSAND = BigDecimal(1000)

	fun formatNumberReadable(number: BigDecimal): String = formatNumberReadable(number, false)

	fun formatNumberReadable(number: BigDecimal, milli: Boolean): String = formatNumberReadable(number, milli, DECIMAL_FORMAT_1F, null)

	fun formatNumberReadable(number: BigDecimal, milli: Boolean, fmt: NumberFormat, @Nullable unit: String?): String {
		val sb = StringBuilder()
		val zero = BigDecimal.ZERO

		// 处理负数
		val absNumber: BigDecimal
		if (number < zero) {
			sb.append('-')
			absNumber = -number
		} else {
			absNumber = number
		}

		// 处理毫单位（除以 1000）：用移动小数点 + 精度截断替代大数除法
		var numberVar = absNumber
		var milliVar = milli
		if (milliVar && numberVar >= ONE_THOUSAND) {
			milliVar = false
			numberVar = numberVar.movePointLeft(3).round(MathContext.DECIMAL128)
		}

		var exp = 0
		var number1 = numberVar // 最终用于格式化的数值

		if (numberVar >= ONE_THOUSAND) {
			// 直接通过整数位数计算千位指数，完全避免 while 循环除法
			val intDigits = getIntegerDigits(numberVar)
			exp = (intDigits - 1) / 3 // 每 3 位一个单位

			if (exp > UNITS.size - 1) {
				return DECIMAL_FORMAT_SIC_2F.format(numberVar)
			}

			// 移动小数点相当于除以 1000^exp，再截断到 34 位有效数字
			number1 = numberVar.movePointLeft(3 * exp).round(MathContext.DECIMAL128)
		}

		sb.append(fmt.format(number1))

		// 追加单位后缀
		if (exp > 0) {
			sb.append(UNITS[exp])
		} else if (milliVar && number1.compareTo(zero) != 0) {
			sb.append('m')
		}
		if (unit != null) sb.append(unit)

		return sb.toString()
	}

	/**
	 * 高效获取 BigDecimal 整数部分的十进制位数，内部仅用 bitLength() 等信息，O(1) 复杂度
	 */
	private fun getIntegerDigits(number: BigDecimal): Int {
		if (number.signum() == 0) return 0
		// 整数位数 = 有效数字总位数（precision） - 小数位数（scale）
		val precision = number.precision() // 对巨型整数，底层仅依赖于 BigInteger.bitLength()
		val scale = number.scale()
		return maxOf(0, precision - scale)
	}

	fun formatNumber(number: Double): String {
		val units = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D")
		var temp = number
		var unitIndex = 0
		while (temp >= 100 && unitIndex < units.size - 1) {
			temp /= 100
			unitIndex++
		}
		return DF_FORMAT_NUMBER.format(temp) + units[unitIndex]
	}
}
