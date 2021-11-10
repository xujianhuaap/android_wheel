package cn.skullmind.widget.wheel

import android.os.Handler
import android.os.Message

internal class MessageHandler(val loopview: WheelView) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            WHAT_INVALIDATE_LOOP_VIEW -> loopview.invalidate()
            WHAT_SMOOTH_SCROLL -> loopview.smoothScroll()
            WHAT_ITEM_SELECTED -> {
                loopview.invalidate()
                loopview.onItemSelected()
                loopview.executePendingAdapter()
            }
        }
    }

    fun cleanMessages() {
        removeMessages(WHAT_INVALIDATE_LOOP_VIEW)
        removeMessages(WHAT_SMOOTH_SCROLL)
        removeMessages(WHAT_ITEM_SELECTED)
    }

    companion object {
        const val WHAT_INVALIDATE_LOOP_VIEW = 1000
        const val WHAT_SMOOTH_SCROLL = 2000
        const val WHAT_ITEM_SELECTED = 3000
    }
}