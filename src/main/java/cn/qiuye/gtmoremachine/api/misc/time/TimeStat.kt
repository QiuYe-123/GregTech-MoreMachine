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

    var avgChanged: BigDecimal = BigDecimal.ZERO
    private var avgInput: BigDecimal = BigDecimal.ZERO
    private var avgOutput: BigDecimal = BigDecimal.ZERO

    fun tick() {
        if (minute.tock()) {
            if (hour.tock()) {
                day.tock()
            }
        }
        val divisor = minute.slotResolution.toLong()
        avgChanged =
            if (lastChangedCache.compareTo(BigInteger.ZERO) ==
                0
            ) {
                BigDecimal.ZERO
            } else {
                BigDecimal(lastChangedCache).divide(
                    BigDecimal.valueOf(divisor),
                    RoundingMode.HALF_UP,
                )
            }
        avgInput =
            if (lastInputCache.compareTo(BigInteger.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                BigDecimal(lastInputCache).divide(
                    BigDecimal.valueOf(divisor),
                    RoundingMode.HALF_UP,
                )
            }
        avgOutput =
            if (lastOutputCache.compareTo(BigInteger.ZERO) ==
                0
            ) {
                BigDecimal.ZERO
            } else {
                BigDecimal(lastOutputCache).divide(
                    BigDecimal.valueOf(divisor),
                    RoundingMode.HALF_UP,
                )
            }

        lastChangedCache = BigInteger.ZERO
        lastInputCache = BigInteger.ZERO
        lastOutputCache = BigInteger.ZERO
    }

    fun update(value: BigInteger, currentTick: Int) {
        minute.update(value, currentTick)
        hour.update(value, currentTick)
        day.update(value, currentTick)
        lastChangedCache = lastChangedCache.add(value)
        if (value > BigInteger.ZERO) {
            lastInputCache = lastInputCache.add(value)
        } else if (value < BigInteger.ZERO) {
            lastOutputCache = lastOutputCache.add(value.negate())
        }
    }

    fun getAvg(statu: Status): BigDecimal = when (statu) {
        All -> this.avgChanged
        In -> this.avgInput
        Out -> this.avgOutput
    }

    fun getMinuteAvg(statu: Status) = getAvg(minute, statu)

    fun getHourAvg(statu: Status) = getAvg(hour, statu)

    fun getDayAvg(statu: Status) = getAvg(day, statu)

    private fun getAvg(timeWheel: TimeWheel, statu: Status) = when (statu) {
        All -> timeWheel.avgChangedByTick
        In -> timeWheel.avgInputByTick
        Out -> timeWheel.avgOutputByTick
    }
}
