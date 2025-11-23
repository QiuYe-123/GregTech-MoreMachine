package cn.qiuye.gtmoremachine.api.misc.wireless.energy

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class EnergyStat(windowStart: Int) {
    private val minute: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.SECOND, 60, windowStart)
    private val hour: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.MINUTE, 60, windowStart)
    private val day: TimeWheel = TimeWheel(TimeWheel.TIMESCALE.HOUR, 24, windowStart)
    private var lastChangedCache: BigInteger = BigInteger.ZERO

    var avgEnergy: BigDecimal = BigDecimal.ZERO

    fun tick() {
        if (minute.tock()) {
            if (hour.tock()) {
                day.tock()
            }
        }
        avgEnergy =
            if (lastChangedCache.compareTo(BigInteger.ZERO) ==
                0
            ) {
                BigDecimal.ZERO
            } else {
                BigDecimal(lastChangedCache).divide(
                    BigDecimal.valueOf(minute.slotResolution.toLong()),
                    RoundingMode.HALF_UP,
                )
            }
        lastChangedCache = BigInteger.ZERO
    }

    fun update(value: BigInteger?, currentTick: Int) {
        minute.update(value, currentTick)
        hour.update(value, currentTick)
        day.update(value, currentTick)
        lastChangedCache = lastChangedCache.add(value)
    }

    val minuteAvg: BigDecimal
        get() = minute.avgByTick

    val hourAvg: BigDecimal
        get() = hour.avgByTick

    val dayAvg: BigDecimal
        get() = day.avgByTick
}
