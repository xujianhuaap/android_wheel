package cn.skullmind.widget.wheel.impl

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface

class PaintOptions internal constructor(
    typeface: Typeface, textColorOut: Int,
    textColorCenter: Int, textColorCenterLabel: Int,
    dividerColor: Int, textSize: Int,
    textSizeLabel: Int
) {
    private var typeface = Typeface.MONOSPACE //字体样式，默认是等宽字体
    val paintOuterText: Paint
    val paintCenterText: Paint
    val paintIndicator: Paint
    val paintCenterTextLabel: Paint
    private var textColorOut: Int
    private var textColorCenter: Int
    private val textColorCenterLabel: Int
    private var dividerColor: Int
    var textSize //选项的文字大小
            : Int
        private set
    private val textSizeLabel: Int
    private fun initPaints() {
        initOutPaint()
        initCenterPaint()
        initCenterPaintForLabel()
        initIndicatorPaint()
    }

    private fun initIndicatorPaint() {
        paintIndicator.color = dividerColor
        paintIndicator.isAntiAlias = true
    }

    private fun initCenterPaintForLabel() {
        paintCenterTextLabel.color = textColorCenterLabel
        paintCenterTextLabel.isAntiAlias = true
        //        paintCenterTextLabel.setTextScaleX(1.1F);
        paintCenterTextLabel.textSize = textSizeLabel.toFloat()
    }

    private fun initCenterPaint() {
        paintCenterText.color = textColorCenter
        paintCenterText.isAntiAlias = true
        //        paintCenterText.setTextScaleX(1.1F);
        paintCenterText.typeface = typeface
        paintCenterText.textSize = textSize.toFloat()
    }

    private fun initOutPaint() {
        paintOuterText.color = textColorOut
        paintOuterText.isAntiAlias = true
        paintOuterText.typeface = typeface
        paintOuterText.textSize = textSize.toFloat()
    }

    fun setTypeface(font: Typeface) {
        typeface = font
        paintOuterText.typeface = typeface
        paintCenterText.typeface = typeface
    }

    fun setTextColorOut(textColorOut: Int) {
        if (textColorOut != 0) {
            this.textColorOut = textColorOut
            paintOuterText.color = this.textColorOut
        }
    }

    fun setTextColorCenter(textColorCenter: Int) {
        if (textColorCenter != 0) {
            this.textColorCenter = textColorCenter
            paintCenterText.color = this.textColorCenter
        }
    }

    fun setDividerColor(dividerColor: Int) {
        if (dividerColor != 0) {
            this.dividerColor = dividerColor
            paintIndicator.color = this.dividerColor
        }
    }

    fun setTextSize(context: Context, size: Float) {
        if (size > 0.0f) {
            textSize = (context.resources.displayMetrics.density * size).toInt()
            paintOuterText.textSize = textSize.toFloat()
            paintCenterText.textSize = textSize.toFloat()
        }
    }

    init {
        this.typeface = typeface
        this.textColorOut = textColorOut
        this.textColorCenter = textColorCenter
        this.textColorCenterLabel = textColorCenterLabel
        this.dividerColor = dividerColor
        this.textSize = textSize
        this.textSizeLabel = textSizeLabel
        paintIndicator = Paint()
        paintCenterTextLabel = Paint()
        paintCenterText = Paint()
        paintOuterText = Paint()
        initPaints()
    }
}