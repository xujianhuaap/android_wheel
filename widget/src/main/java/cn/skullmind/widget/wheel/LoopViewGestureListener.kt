package cn.skullmind.widget.wheel

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

class LoopViewGestureListener internal constructor(val loopView: WheelView) :
    SimpleOnGestureListener() {
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        loopView.scrollBy(velocityY)
        return true
    }
}