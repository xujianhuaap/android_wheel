package cn.skullmind.widget.wheel.impl

import android.content.Context
import android.graphics.Typeface
import cn.skullmind.widget.wheel.WheelAdapter
import cn.skullmind.widget.wheel.WheelView

class WheelOptions(
    val paintOptions: PaintOptions,
    val initOptions: InitOptions,
    val wheelViewMeasure: WheelViewMeasure,
    selectListener: SelectItemState.SelectListener
) {
    lateinit var  drawOptions: DrawOptions
        private set
    val wheelContentMeasure: WheelContentMeasure
    val wheelLabelMeasure: WheelLabelMeasure
    val scrollStatus: ScrollStatus
    val selectItemState: SelectItemState
    fun reset() {
        wheelViewMeasure.resetHalfCircumference()
    }

    fun <T> setPendingAdapter(adapter: WheelAdapter<T>, currentStatus: WheelView.STATUS) {
        selectItemState.setPendingAdapter(adapter, currentStatus)
    }

    fun executePendingAdapter() {
        selectItemState.executePendingAdapter()
    }

    fun setValidSelectedItem(validSelectedItem: Int) {
        selectItemState.validSelectedItem = validSelectedItem
    }

    fun <T> setValidAdapter(validAdapter: WheelAdapter<T>) {
        selectItemState.setValidAdapter(validAdapter)
    }

    @Synchronized
    fun setSelectedItem(selectedItem: Int) {
        selectItemState.setSelectedItem(selectedItem)
    }

    fun setInitPosition(initPosition: Int) {
        scrollStatus.initPosition = initPosition
    }

    fun setTypeface(font: Typeface) {
        paintOptions.setTypeface(font)
    }

    fun setTextSize(context: Context?, size: Float) {
        paintOptions.setTextSize(context!!, size)
    }

    fun setDividerColor(dividerColor: Int) {
        paintOptions.setDividerColor(dividerColor)
    }

    fun setTextColorCenter(textColorCenter: Int) {
        paintOptions.setTextColorCenter(textColorCenter)
    }

    fun setTextColorOut(textColorOut: Int) {
        paintOptions.setTextColorOut(textColorOut)
    }

    fun setLoop(isLoop: Boolean) {
        initOptions.isLoop = isLoop
    }

    fun setGravity(gravity: Int) {
        initOptions.gravity = gravity
    }

    fun setIsOptions(options: Boolean) {
        initOptions.isOptions = options
    }

    fun setDividerType(dividerType: WheelView.DividerType) {
        initOptions.dividerType = dividerType
    }

    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float) {
        if (lineSpacingMultiplier != 0f) {
            initOptions.lineSpacingMultiplier = lineSpacingMultiplier
        }
    }

    fun isCenterLabel(isCenterLabel: Boolean?) {
        initOptions.isCenterLabel = isCenterLabel!!
    }

    fun setLabel(label: String?) {
        initOptions.label = label
    }

    fun setStartCenter(startCenter: Boolean) {
        initOptions.isStartCenter = startCenter
    }

    //TODO
    val rawWorkingAdapter: WheelAdapter<Any>
        get() =//TODO
            selectItemState.getWorkingAdapter() as WheelAdapter<Any>
    val wheelAdapter: WheelAdapter<Any>?
        get() {
            val workingAdapter: WheelAdapter<Any> = rawWorkingAdapter
            return if (workingAdapter == null || workingAdapter.itemsCount <= 0) {
                null
            } else workingAdapter
        }
    val itemHeight: Float
        get() = wheelContentMeasure.getItemHeight(paintOptions.paintCenterText)
    val itemsCount: Int
        get() {
            val workingAdapter: WheelAdapter<*> = rawWorkingAdapter
            return if (workingAdapter != null) workingAdapter.itemsCount else 0
        }

    @get:Synchronized
    val selectItemContent: Any?
        get() = selectItemState.selectItemContent

    fun remeasureIfNeed(viewHeight: Int) {
        if (wheelViewMeasure.measuredHeight != viewHeight) {
            wheelViewMeasure.reMeasureHeight(viewHeight)
            remeasure(viewHeight)
        }
    }

    fun remeasureWhenChange(widthMeasureSpec: Int, viewHeight: Int) {
        wheelViewMeasure.refreshMeasureWidth(widthMeasureSpec)
        remeasure(viewHeight)
    }

    fun remeasure(viewHeight: Int) { //重新测量
        val workingAdpater: WheelAdapter<*> = rawWorkingAdapter ?: return
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        //半圆的周长 = item高度乘以item数目-1
        val paintCenterText = paintOptions.paintCenterText
        val itemHeight = wheelContentMeasure.getItemHeight(paintCenterText)
        wheelViewMeasure.remeasureHalfCircumference(itemHeight)
        wheelViewMeasure.reMeasureHeight(viewHeight)
        wheelViewMeasure.refreshItemVisibleCount(itemHeight)
        val halfCircumference = wheelViewMeasure.halfCircumference
        val itemVisibleCount = wheelViewMeasure.itemVisibleCount
        scrollStatus.refreshInitStartPoint(
            itemHeight.toDouble(),
            halfCircumference,
            itemVisibleCount
        )
        drawOptions = DrawOptions(wheelViewMeasure, wheelContentMeasure, paintOptions)
        drawOptions!!.refreshLines()
        //初始化显示的item的position
        val itemsCount: Int = workingAdpater.itemsCount
        scrollStatus.refreshInitPosition(initOptions.isLoop, itemsCount)
        scrollStatus.initPreCurrentIndex()
        selectItemState.setSelectedItem(scrollStatus.preCurrentIndex)
    }

    init {
        wheelContentMeasure = WheelContentMeasure(paintOptions, initOptions)
        wheelLabelMeasure = WheelLabelMeasure(initOptions)
        scrollStatus = ScrollStatus()
        selectItemState = SelectItemState(selectListener)
    }
}