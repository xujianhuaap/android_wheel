package cn.skullmind.widget.wheel.impl

import android.graphics.Typeface

class PaintOptionsBuilder {
    private var typeface: Typeface? = null
    private var textColorOut = 0
    private var textColorCenter = 0
    private var textColorCenterLabel = 0
    private var dividerColor = 0
    private var textSize = 0
    private var textSizeLabel = 0
    fun setTypeface(typeface: Typeface?): PaintOptionsBuilder {
        this.typeface = typeface
        return this
    }

    fun setTextColorOut(textColorOut: Int): PaintOptionsBuilder {
        this.textColorOut = textColorOut
        return this
    }

    fun setTextColorCenter(textColorCenter: Int): PaintOptionsBuilder {
        this.textColorCenter = textColorCenter
        return this
    }

    fun setTextColorCenterLabel(textColorCenterLabel: Int): PaintOptionsBuilder {
        this.textColorCenterLabel = textColorCenterLabel
        return this
    }

    fun setDividerColor(dividerColor: Int): PaintOptionsBuilder {
        this.dividerColor = dividerColor
        return this
    }

    fun setTextSize(textSize: Int): PaintOptionsBuilder {
        this.textSize = textSize
        return this
    }

    fun setTextSizeLabel(textSizeLabel: Int): PaintOptionsBuilder {
        this.textSizeLabel = textSizeLabel
        return this
    }

    fun createContentPaint(): PaintOptions {
        if (typeface == null) typeface = Typeface.MONOSPACE
        if (textColorOut == 0) textColorOut = -0x86796f
        if (textColorCenter == 0) textColorCenter = -0xffdcb2
        if (textColorCenterLabel == 0) textColorCenterLabel = -0xffdcb2
        if (dividerColor == 0) dividerColor = -0x2a2a2b
        return PaintOptions(
            typeface!!,
            textColorOut,
            textColorCenter,
            textColorCenterLabel,
            dividerColor,
            textSize,
            textSizeLabel
        )
    }
}