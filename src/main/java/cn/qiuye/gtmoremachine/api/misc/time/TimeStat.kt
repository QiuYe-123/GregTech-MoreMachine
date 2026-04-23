package cn.qiuye.gtmoremachine.api.misc.time

import cn.qiuye.gtmoremachine.api.gui.monitor.Status
import cn.qiuye.gtmoremachine.api.gui.monitor.Status.*

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class TimeStat(windowStart: Int = 0) {
	private val minute: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.SECOND, 60, windowStart)
	private val hour: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.MINUTE, 60, windowStart)
	private val day: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.HOUR, 24, windowStart)
	private var lastChangedCache: BigInteger = BigInteger.ZERO
	private var lastInputCache: BigInteger = BigInteger.ZERO
	private var lastOutputCache: BigInteger = BigInteger.ZERO

	private var avgChanged: BigDecimal = BigDecimal.ZERO
	private var avgInput: BigDecimal = BigDecimal.ZERO
	private var avgOutput: BigDecimal = BigDecimal.ZERO

	fun tick() {
		if (this.minute.tock()) {
			if (this.hour.tock()) {
				this.day.tock()
			}
		}
		val divisor = this.minute.slotResolution.toLong()
		this.avgChanged =
			if (this.lastChangedCache.compareTo(BigInteger.ZERO) ==
				0
			) {
				BigDecimal.ZERO
			} else {
				BigDecimal(this.lastChangedCache).divide(
					BigDecimal.valueOf(divisor),
					RoundingMode.HALF_UP,
				)
			}
		this.avgInput =
			if (this.lastInputCache.compareTo(BigInteger.ZERO) == 0) {
				BigDecimal.ZERO
			} else {
				BigDecimal(this.lastInputCache).divide(
					BigDecimal.valueOf(divisor),
					RoundingMode.HALF_UP,
				)
			}
		this.avgOutput =
			if (this.lastOutputCache.compareTo(BigInteger.ZERO) ==
				0
			) {
				BigDecimal.ZERO
			} else {
				BigDecimal(this.lastOutputCache).divide(
					BigDecimal.valueOf(divisor),
					RoundingMode.HALF_UP,
				)
			}

		this.lastChangedCache = BigInteger.ZERO
		this.lastInputCache = BigInteger.ZERO
		this.lastOutputCache = BigInteger.ZERO
	}

	fun update(value: BigInteger, currentTick: Int) {
		this.minute.update(value, currentTick)
		this.hour.update(value, currentTick)
		this.day.update(value, currentTick)
		this.lastChangedCache = this.lastChangedCache.add(value)
		if (value > BigInteger.ZERO) {
			this.lastInputCache = this.lastInputCache.add(value)
		} else if (value < BigInteger.ZERO) {
			this.lastOutputCache = this.lastOutputCache.add(value.negate())
		}
	}

	fun getAvg(statu: Status): BigDecimal = when (statu) {
		All -> this.avgChanged
		In -> this.avgInput
		Out -> this.avgOutput
	}

	fun getMinuteAvg(statu: Status) = getAvg(this.minute, statu)

	fun getHourAvg(statu: Status) = getAvg(this.hour, statu)

	fun getDayAvg(statu: Status) = getAvg(this.day, statu)

	private fun getAvg(timeWheel: TimeWheel, statu: Status) = when (statu) {
		All -> timeWheel.avgChangedByTick
		In -> timeWheel.avgInputByTick
		Out -> timeWheel.avgOutputByTick
	}
}
