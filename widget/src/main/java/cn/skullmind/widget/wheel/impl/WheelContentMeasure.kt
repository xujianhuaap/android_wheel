package cn.skullmind.widget.wheel.impl

import android.graphics.Paint
import android.graphics.Rect
import android.view.Gravity
import cn.skullmind.widget.wheel.WheelAdapter
import cn.skullmind.widget.wheel.impl.WheelUtils.getContentText

class WheelContentMeasure(
    private val paintOptions: PaintOptions,
    private val initOptions: InitOptions
) {
    fun measureTextWidth(paint: Paint, workingAdapter: WheelAdapter<Any>): Int {
        val rect = Rect()
        var maxTextWidth = 0
        for (i in 0 until workingAdapter.itemsCount) {
            val s1: String = getContentText(workingAdapter.getItem(i))
            paint.getTextBounds(s1, 0, s1.length, rect)
            val textWidth = rect.width()
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
            paint.getTextBounds("\u661F\u671F", 0, 2, rect) // 星期的字符编码（以它为标准高度）
        }
        return maxTextWidth
    }

    fun getTextHeight(paint: Paint): Int {
        val rect = Rect()
        paint.getTextBounds("\u661F\u671F", 0, 2, rect) // 星期的字符编码（以它为标准高度）
        return rect.height() + 2
    }

    fun getItemHeight(paint: Paint): Float {
        return initOptions.lineSpacingMultiplier * getTextHeight(paint)
    }

    fun getOutContentStart(
        content: String,
        measureWidth: Float,
        CENTERCONTENTOFFSET: Float
    ): Double {
        val label = initOptions.label
        val rect = Rect()
        paintOptions.paintOuterText.getTextBounds(content, 0, content.length, rect)
        var drawOutContentStart = 0.0
        when (initOptions.gravity) {
            Gravity.CENTER -> drawOutContentStart = if (initOptions.isStartCenter) {
                ((measureWidth - rect.width()) / 2).toDouble()
            } else if (initOptions.isOptions || label == null || label == "" || !initOptions.isCenterLabel) {
                ((measureWidth - rect.width()) * 0.5)
            } else { //只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                ((measureWidth - rect.width()) * 0.25)
            }
            Gravity.START -> drawOutContentStart = 0.0
            Gravity.END -> drawOutContentStart =
                (measureWidth - rect.width() - CENTERCONTENTOFFSET.toInt()).toDouble()
        }
        return drawOutContentStart
    }

    fun getCenterContentStart(
        content: String,
        measureWidth: Float,
        CENTERCONTENTOFFSET: Float
    ): Double {
        val label = initOptions.label
        val rect = Rect()
        paintOptions.paintCenterText.getTextBounds(content, 0, content.length, rect)
        var drawCenterContentStart = 0.0
        when (initOptions.gravity) {
            Gravity.CENTER -> drawCenterContentStart = if (initOptions.isStartCenter) {
                ((measureWidth - rect.width()) / 2).toDouble()
            } else if (initOptions.isOptions || label == null || label == "" || !initOptions.isCenterLabel) {
                ((measureWidth - rect.width()) * 0.5)
            } else { //只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                ((measureWidth - rect.width()) * 0.25)
            }
            Gravity.START -> drawCenterContentStart = 0.0
            Gravity.END -> drawCenterContentStart =
                (measureWidth - rect.width() - CENTERCONTENTOFFSET.toInt()).toDouble()
        }
        return drawCenterContentStart
    }
}