package cn.skullmind.widget.wheel.impl

import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils

class WheelLabelMeasure(private val initOptions: InitOptions) {
    fun measureLableHeight(paint: Paint): Int {
        if (!TextUtils.isEmpty(initOptions.label)) {
            val rect = Rect()
            paint.getTextBounds(initOptions.label, 0, initOptions.label!!.length, rect)
            return rect.height() + 2
        }
        return 0
    }

    fun measureLabelWidth(paint: Paint): Int {
        if (!TextUtils.isEmpty(initOptions.label)) {
            val rect = Rect()
            paint.getTextBounds(initOptions.label, 0, initOptions.label!!.length, rect)
            return rect.width()
        }
        return 0
    }
}