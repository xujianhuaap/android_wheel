package cn.skullmind.widget.wheel

import java.util.*

/**
 *
 */
class SmoothScrollTimerTask(var offset: Int, listener: Listener) : TimerTask() {
    var realTotalOffset: Int
    var realOffset: Int
    private val listener: Listener
    override fun run() {
        if (realTotalOffset == Int.MAX_VALUE) {
            realTotalOffset = offset
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = (realTotalOffset.toFloat() * 0.1f).toInt()
        if (realOffset == 0) {
            realOffset = if (realTotalOffset < 0) {
                -1
            } else {
                1
            }
        }
        realTotalOffset = ref(realTotalOffset, realOffset)
    }

    private fun ref(realTotalOffset: Int, realOffset: Int): Int {
        var realTotalOffset = realTotalOffset
        if (Math.abs(realTotalOffset) <= 1) {
            listener.endScroll()
        } else {
            if (listener.scrolling(realTotalOffset, realOffset)) return realTotalOffset
            realTotalOffset = realTotalOffset - realOffset
        }
        return realTotalOffset
    }

    interface Listener {
        fun endScroll()
        fun scrolling(realTotalOffset: Int, realOffset: Int): Boolean
    }

    init {
        realTotalOffset = Int.MAX_VALUE
        realOffset = 0
        this.listener = listener
    }
}