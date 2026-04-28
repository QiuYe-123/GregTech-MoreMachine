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

	/** 预计算 1000^0 ~ 1000^(UNITS.size-1)，由 lazy 延迟初始化，供 formatNumberReadable 和 calculateExponent 复用 */
	private val POWERS by lazy {
		Array(UNITS.size) { ONE_THOUSAND.pow(it) }
	}

	fun formatNumberReadable(number: BigDecimal): String = formatNumberReadable(number, false)

	fun formatNumberReadable(number: BigDecimal, milli: Boolean): String = formatNumberReadable(number, milli, DECIMAL_FORMAT_1F, null)

	fun formatNumberReadable(number: BigDecimal, milli: Boolean, fmt: NumberFormat, @Nullable unit: String?): String {
		val sb = StringBuilder()
		val zero = BigDecimal.ZERO
		var number1 = number
		var numberVar = number
		if (numberVar < zero) {
			numberVar = numberVar.abs()
			sb.append('-')
		}
		var milliVar = milli
		if (milliVar && numberVar >= ONE_THOUSAND) {
			milliVar = false
			numberVar = numberVar.divide(ONE_THOUSAND, MathContext.DECIMAL128)
		}
		var exp = 0
		if (numberVar >= ONE_THOUSAND) {
			exp = calculateExponent(numberVar)

			// 当指数超过单位数组范围时使用科学计数法
			if (exp > UNITS.size - 1) {
				return DECIMAL_FORMAT_SIC_2F.format(numberVar)
			}

			// 使用BigDecimal进行幂运算
			if (exp > 0) {
				val divisor = POWERS[exp]
				number1 = numberVar.divide(divisor, MathContext.DECIMAL128)
			}
		}
		sb.append(fmt.format(number1))
		if (exp > 0) {
			sb.append(UNITS[exp])
		} else if (milliVar && number1.compareTo(zero) != 0) {
			sb.append('m')
		}
		if (unit != null) sb.append(unit)
		return sb.toString()
	}

	/**
	 * 使用BigDecimal计算指数，避免double精度限制
	 */
	private fun calculateExponent(number: BigDecimal): Int {
		var exponent = 0
		var temp = number

		while (temp >= ONE_THOUSAND) {
			temp = temp.divide(ONE_THOUSAND, MathContext.DECIMAL128)
			exponent++
		}
		return exponent
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
