package cn.qiuye.gtmoremachine.api.misc.time

import org.jetbrains.annotations.ApiStatus

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.*

class TimeWheel(slotResolution: Int, private val slotNum: Int, windowStart: Int) {
	private var firstUpdateTick = -1
	private var lastUpdateTick = -1

	object TIMESCALE {
		const val SECOND: Int = 20
		const val MINUTE: Int = 20 * 60
		const val HOUR: Int = 20 * 60 * 60
	}

	var slotResolution: Int = if (slotResolution <= 0) 20 else slotResolution
	var slots: ArrayDeque<Slot>
	private var changedSum: BigInteger = BigInteger.ZERO
	private var inputSum: BigInteger = BigInteger.ZERO
	private var outputSum: BigInteger = BigInteger.ZERO
	private val startIndex: Int = (windowStart / slotResolution) % slotNum
	private var currentIndex: Int

	init {
		this.currentIndex = this.startIndex
		this.slots = ArrayDeque<Slot>(this.slotNum)
		this.slots.offer(Slot())
	}

	fun tock(): Boolean {
		if (this.slots.size == this.slotNum) {
			val s: Slot = this.slots.poll()
			this.changedSum = this.changedSum.subtract(s.inputSum).add(s.outputSum)
			this.inputSum = this.inputSum.subtract(s.inputSum)
			this.outputSum = this.outputSum.subtract(s.outputSum)
		}
		this.slots.offer(Slot())
		this.currentIndex = (this.currentIndex + 1) % this.slotNum
		return this.currentIndex == this.startIndex
	}

	fun update(value: BigInteger, currentTick: Int) {
		val slot: Slot = this.slots.peekLast() ?: return
		if (value > BigInteger.ZERO) {
			slot.inputSum = slot.inputSum.add(value)
			this.inputSum = this.inputSum.add(value)
			this.changedSum = this.changedSum.add(value)
		} else if (value < BigInteger.ZERO) {
			val positiveValue = value.negate()
			slot.outputSum = slot.outputSum.add(positiveValue)
			this.outputSum = this.outputSum.add(positiveValue)
			this.changedSum = this.changedSum.add(value)
		}
		this.lastUpdateTick = currentTick
		if (this.firstUpdateTick == -1) this.firstUpdateTick = this.lastUpdateTick
	}

	@get:ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
	@Deprecated(
		message = "Use avgChangedByTick instead. This property will be removed in 2.1.0.",
		replaceWith = ReplaceWith("avgChangedByTick"),
		level = DeprecationLevel.ERROR,
	)
	val avgByTick: BigDecimal
		@Deprecated(
			message = "Use avgChangedByTick instead. This property will be removed in 2.1.0.",
			replaceWith = ReplaceWith("getAvgChangedByTick()"),
			level = DeprecationLevel.ERROR,
		)
		get() {
			if (lastUpdateTick - firstUpdateTick < slotResolution * slotNum) {
				return BigDecimal(changedSum).divide(
					BigDecimal.valueOf(
						(lastUpdateTick - firstUpdateTick + 1).toLong(),
					),
					RoundingMode.HALF_UP,
				)
			}
			return if (slots.isEmpty()) {
				BigDecimal.ZERO
			} else {
				BigDecimal(changedSum).divide(
					BigDecimal.valueOf(
						slots.size.toLong() * slotResolution + lastUpdateTick % slotResolution - slotResolution,
					),
					RoundingMode.HALF_UP,
				)
			}
		}

	/** 获取变化量（净流量）平均值 / 刻 */
	val avgChangedByTick
		get() = calculateAvg(this.changedSum)

	/** 获取输入平均值 / 刻 */
	val avgInputByTick
		get() = calculateAvg(this.inputSum)

	/** 获取输出平均值 / 刻 */
	val avgOutputByTick
		get() = calculateAvg(this.outputSum)

	/** 获取每个时间片内的净变化量历史（inputSum - outputSum） */
	val changedHistory
		get() = this.slots.map { it.inputSum - it.outputSum }

	/** 获取每个时间片内的输入量历史 */
	val inputHistory
		get() = this.slots.map { it.inputSum }

	/** 获取每个时间片内的输出量历史 */
	val outputHistory
		get() = this.slots.map { it.outputSum }

	// 私有辅助方法：计算实际覆盖的时间跨度（刻数）
	private fun totalTimeSpan(): Long {
		if (this.firstUpdateTick == -1) return 0L
		val ticksElapsed = this.lastUpdateTick - this.firstUpdateTick + 1
		val windowLength = this.slotResolution.toLong() * this.slotNum

		return if (ticksElapsed < windowLength) {
			ticksElapsed.toLong()
		} else {
			(this.slotNum - 1).toLong() * this.slotResolution +
				(this.lastUpdateTick % this.slotResolution) + 1
		}
	}

	// 统一的平均值计算方法，复用了时间跨度逻辑
	private fun calculateAvg(total: BigInteger): BigDecimal {
		if (total == BigInteger.ZERO) return BigDecimal.ZERO
		val totalSpan = totalTimeSpan()
		return if (totalSpan <= 0) {
			BigDecimal.ZERO
		} else {
			BigDecimal(total).divide(BigDecimal.valueOf(totalSpan), 2, RoundingMode.HALF_UP)
		}
	}
}
