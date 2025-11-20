package cn.qiuye.gtmoremachine.api.misc

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
    var slots: ArrayDeque<Slot?>
    private var sum: BigInteger = BigInteger.ZERO
    private val startIndex: Int = (windowStart / slotResolution) % slotNum
    private var currentIndex: Int

    init {
        this.currentIndex = startIndex
        slots = ArrayDeque<Slot?>(slotNum)
        slots.offer(Slot())
    }

    fun tock(): Boolean {
        if (slots.size == slotNum) {
            val s: Slot? = slots.poll()
            if (s != null) {
                sum = sum.subtract(s.sum)
            }
        }
        slots.offer(Slot())
        currentIndex = (currentIndex + 1) % slotNum
        return currentIndex == startIndex
    }

    fun update(value: BigInteger?, currentTick: Int) {
        val slot: Slot = slots.peekLast() ?: return
        slot.sum = slot.sum.add(value)
        sum = sum.add(value)
        this.lastUpdateTick = currentTick
        if (firstUpdateTick == -1) firstUpdateTick = lastUpdateTick
    }

    val avgByTick: BigDecimal
        get() {
            if (lastUpdateTick - firstUpdateTick < slotResolution * slotNum) {
                return BigDecimal(sum).divide(
                    BigDecimal.valueOf(
                        (lastUpdateTick - firstUpdateTick + 1).toLong(),
                    ),
                    RoundingMode.HALF_UP,
                )
            }
            return if (slots.isEmpty()) {
                BigDecimal.ZERO
            } else {
                BigDecimal(sum).divide(
                    BigDecimal.valueOf(
                        slots.size.toLong() * slotResolution + lastUpdateTick % slotResolution - slotResolution,
                    ),
                    RoundingMode.HALF_UP,
                )
            }
        }
}
