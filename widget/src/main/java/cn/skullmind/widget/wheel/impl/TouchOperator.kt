package cn.skullmind.widget.wheel.impl

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import cn.skullmind.widget.wheel.LoopViewGestureListener
import cn.skullmind.widget.wheel.WheelView

class TouchOperator(context: Context?, listener: LoopViewGestureListener?) {
    private val gestureDetector: GestureDetector

    //滚动总高度y值
    var totalScrollY = 0f
    private var previousY = 0f
    private var moveStartY = 0f
    private var moveEndtY = 0f
    var offset = 0
        private set
    private var startTime: Long = 0
    fun handleTouchEvent(
        event: MotionEvent,
        wheelOptions: WheelOptions
    ) {
        val initOptions = wheelOptions.initOptions
        val paintOptions = wheelOptions.paintOptions
        val wheelContentMeasure = wheelOptions.wheelContentMeasure
        val itemsCount: Int = wheelOptions.rawWorkingAdapter.itemsCount
        val initPosition = wheelOptions.scrollStatus.initPosition
        val eventConsumed: Boolean = gestureDetector.onTouchEvent(event)
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                previousY = event.getRawY()
                moveStartY = event.getRawY()
            }
            MotionEvent.ACTION_MOVE -> {
                val dy: Float = previousY - event.getRawY()
                Log.d(WheelView::class.java.getName(), "dy: $dy totalY $totalScrollY")
                previousY = event.getRawY()
                totalScrollY = totalScrollY + dy

                // 边界处理。
                if (!initOptions.isLoop) {
                    val paint = paintOptions.paintCenterText
                    val top = -initPosition * wheelContentMeasure.getItemHeight(paint)
                    val bottom =
                        (itemsCount - 1 - initPosition) * wheelContentMeasure.getItemHeight(paint)
                    if (totalScrollY < top) {
                        totalScrollY = top
                    }
                    if (totalScrollY > bottom) {
                        totalScrollY = bottom
                    }
                }
            }
            MotionEvent.ACTION_UP -> if (!eventConsumed) { //未消费掉事件
                moveEndtY = event.getRawY()
                val value = moveStartY - moveEndtY
                val isMove = Math.abs(value) > 3
                if (isMove) {
                    if (System.currentTimeMillis() - startTime > 120) {
                        // 处理拖拽事件
                        refreshOffSet(WheelView.ACTION.DAGGLE, paintOptions, wheelContentMeasure)
                    } else {
                        // 处理条目点击事件
                        refreshOffSet(WheelView.ACTION.CLICK, paintOptions, wheelContentMeasure)
                    }
                }
            }
            else -> if (!eventConsumed) {
                moveEndtY = event.getRawY()
                val value = moveStartY - moveEndtY
                val isMove = Math.abs(value) > 3
                if (isMove) {
                    if (System.currentTimeMillis() - startTime > 120) {
                        refreshOffSet(WheelView.ACTION.DAGGLE, paintOptions, wheelContentMeasure)
                    } else {
                        refreshOffSet(WheelView.ACTION.CLICK, paintOptions, wheelContentMeasure)
                    }
                }
            }
        }
    }

    fun refreshOffSet(
        action: WheelView.ACTION, paintOptions: PaintOptions,
        wheelContentMeasure: WheelContentMeasure
    ) { //平滑滚动的实现
        if (action === WheelView.ACTION.FLING || action === WheelView.ACTION.DAGGLE) {
            val paintCenterText = paintOptions.paintCenterText
            offset = (totalScrollY % wheelContentMeasure.getItemHeight(paintCenterText)
                .toDouble()).toInt()
            if (Math.abs(offset).toFloat() > wheelContentMeasure.getItemHeight(paintCenterText)
                    .toDouble() / 2.0f
            ) { //如果超过Item高度的一半，滚动到下一个Item去
                if (offset < 0) {
                    offset = (-(wheelContentMeasure.getItemHeight(paintCenterText)
                        .toDouble() + offset)).toInt()
                } else {
                    offset = (wheelContentMeasure.getItemHeight(paintCenterText)
                        .toDouble() - offset).toInt()
                }
            } else {
                offset = -offset
            }
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
    }

    fun reset() {
        totalScrollY = 0f
        moveStartY = 0f
        moveEndtY = 0f
        previousY = 0f
        startTime = 0
    }

    init {
        gestureDetector = GestureDetector(context, listener)
        gestureDetector.setIsLongpressEnabled(false)
    }
}