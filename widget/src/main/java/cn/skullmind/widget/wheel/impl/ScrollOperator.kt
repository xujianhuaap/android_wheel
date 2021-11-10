package cn.skullmind.widget.wheel.impl

import cn.skullmind.widget.wheel.InertiaTimerTask
import cn.skullmind.widget.wheel.SmoothScrollTimerTask
import cn.skullmind.widget.wheel.WheelView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ScrollOperator(private val listener: ScrollOperatorListener) {
    // Timer mTimer;
    private val mExecutor = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null
    fun smoothScroll(action: WheelView.ACTION?, taskListener: SmoothScrollTimerTask.Listener) {
        cancelFuture()
        val offset = listener.getOffSet(action)
        mFuture = mExecutor.scheduleWithFixedDelay(
            SmoothScrollTimerTask(
                offset, taskListener
            ), 0, 10, TimeUnit.MILLISECONDS
        )
    }

    fun scrollBy(velocityY: Float, taskListener: InertiaTimerTask.Listener) {
        //滚动惯性的实现
        listener.scrollBy(velocityY)
        cancelFuture()
        mFuture = mExecutor.scheduleWithFixedDelay(
            InertiaTimerTask(velocityY, taskListener),
            0, VELOCITYFLING.toLong(), TimeUnit.MILLISECONDS
        )
    }

    fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }

    interface ScrollOperatorListener {
        fun getOffSet(action: WheelView.ACTION?): Int
        fun scrollBy(velocityY: Float)
    }

    companion object {
        // 修改这个值可以改变滑行速度
        private const val VELOCITYFLING = 5
    }
}