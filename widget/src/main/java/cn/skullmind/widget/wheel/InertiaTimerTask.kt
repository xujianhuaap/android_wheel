package cn.skullmind.widget.wheel

import java.util.*


class InertiaTimerTask(val velocityY: Float, listener: Listener) : TimerTask() {
    var pendingScrollValue: Float
    private val listener: Listener
    override fun run() {
        if (pendingScrollValue == Int.MAX_VALUE.toFloat()) {
            pendingScrollValue = if (Math.abs(velocityY) > 2000f) {
                if (velocityY > 0.0f) {
                    2000f
                } else {
                    -2000f
                }
            } else {
                velocityY
            }
        }
        if (Math.abs(pendingScrollValue) >= 0.0f && Math.abs(pendingScrollValue) <= 20f) {
            listener.executeSmoothScroll()
            return
        }
        val i = (pendingScrollValue * 10f / 1000f).toInt()
        pendingScrollValue = listener.refreshTotalScrollY(i, pendingScrollValue)
        listener.refreshViewAfterLongScroll()
    }

    interface Listener {
        fun executeSmoothScroll()
        fun refreshViewAfterLongScroll()
        fun refreshTotalScrollY(i: Int, initPendingScrollValue: Float): Float
    }

    init {
        pendingScrollValue = Int.MAX_VALUE.toFloat()
        this.listener = listener
    }
}