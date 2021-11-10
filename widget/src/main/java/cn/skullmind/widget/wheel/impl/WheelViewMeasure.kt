package cn.skullmind.widget.wheel.impl

import android.view.View

class WheelViewMeasure(var measuredHeight: Int) {
    var measureWidth = 0
        private set

    // 半圆周长
    var halfCircumference = 0.0
        private set
    var itemVisibleCount = 0
        private set

    fun reMeasureHeight(currentViewHeight: Int) {
        if (currentViewHeight != measuredHeight) {
            measuredHeight = currentViewHeight
        }
        if (measuredHeight <= 0) {
            measuredHeight = (halfCircumference * 2f / Math.PI).toInt()
        }
    }

    fun remeasureHalfCircumference(itemHeight: Float) {
        halfCircumference = if (measuredHeight <= 0) {
            (DEFAULT_ITEM_VISILES * itemHeight).toDouble()
        } else {
            Math.PI * measuredHeight / 2
        }
    }

    fun resetHalfCircumference() {
        halfCircumference = 0.0
    }

    fun refreshItemVisibleCount(itemHeight: Float) {
        val itemsVisible = (halfCircumference / itemHeight).toInt()
        val isEven = itemsVisible % 2 == 0
        itemVisibleCount = if (isEven) itemsVisible + 3 else itemsVisible + 2
    }

    //求出半径
    val radius: Int
        get() =//求出半径
            (halfCircumference / Math.PI).toInt()

    fun refreshMeasureWidth(widthMeasureSpec: Int) {
        measureWidth = View.MeasureSpec.getSize(widthMeasureSpec)
    }

    companion object {
        private const val DEFAULT_ITEM_VISILES = 9
    }
}