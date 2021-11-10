package cn.skullmind.widget.wheel.impl

import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextUtils
import cn.skullmind.widget.wheel.WheelAdapter
import cn.skullmind.widget.wheel.WheelView
import cn.skullmind.widget.wheel.impl.WheelUtils.getContentText
import cn.skullmind.widget.wheel.impl.WheelUtils.getTextWidth

class DrawOperator(wheelOptions: WheelOptions) {
    private val initOptions: InitOptions
    private val drawOptions: DrawOptions
    private val paintOptions: PaintOptions
    private val wheelContentMeasure: WheelContentMeasure
    private val wheelViewMeasure: WheelViewMeasure
    private val wheelLabelMeasure: WheelLabelMeasure
    private fun drawLines(canvas: Canvas, workingAdapter: WheelAdapter<Any>) {
        val measureWidth = wheelViewMeasure.measureWidth.toFloat()
        val firstLineY = drawOptions.firstLineY
        val secondLineY = drawOptions.secondLineY
        val paintCenterText = paintOptions.paintCenterText
        val paintIndicator = paintOptions.paintIndicator
        if (initOptions.dividerType === WheelView.DividerType.WRAP) { //横线长度仅包裹内容
            var startX = 0f
            val endX: Float
            if (TextUtils.isEmpty(initOptions.label)) { //隐藏Label的情况
                startX = (measureWidth - wheelContentMeasure.measureTextWidth(
                    paintCenterText,
                    workingAdapter
                )) / 2 - 12
            }
            if (startX <= 0) { //如果超过了WheelView的边缘
                startX = 10f
            }
            endX = measureWidth - startX
            canvas.drawLine(
                startX,
                firstLineY.toFloat(),
                endX,
                firstLineY.toFloat(),
                paintIndicator
            )
            canvas.drawLine(
                startX,
                secondLineY.toFloat(),
                endX,
                secondLineY.toFloat(),
                paintIndicator
            )
        } else {
            canvas.drawLine(
                0.0f,
                firstLineY.toFloat(),
                measureWidth,
                firstLineY.toFloat(),
                paintIndicator
            )
            canvas.drawLine(
                0.0f,
                secondLineY.toFloat(),
                measureWidth,
                secondLineY.toFloat(),
                paintIndicator
            )
        }
    }

    private fun drawLabel(
        canvas: Canvas, drawCenterContentStart: Double,
        workingAdapter: WheelAdapter<*>
    ) {
        val paintCenterText = paintOptions.paintCenterText
        val centerY = drawOptions.centerY
        //只显示选中项Label文字的模式，并且Label文字不为空，则进行绘制
        if (!TextUtils.isEmpty(initOptions.label) && initOptions.isCenterLabel) {
            //绘制文字，靠右并留出空隙
            val drawLabel = getDrawLabel(workingAdapter)
            val drawRightContentStart: Int =
                drawCenterContentStart.toInt() + getTextWidth(paintCenterText, drawLabel)
            val paintCenterTextLabel = paintOptions.paintCenterTextLabel
            canvas.drawText(
                initOptions.label!!,
                drawRightContentStart.toFloat() + initOptions.drawContentOffSet * 3.0f,
                centerY.toFloat() + (wheelLabelMeasure.measureLableHeight(paintCenterTextLabel) / 2).toFloat(),
                paintCenterTextLabel
            )
        }
    }

    private fun getDrawLabel(workingAdapter: WheelAdapter<*>?): String {
        return if (workingAdapter != null && workingAdapter.itemsCount > 0) {
            getContentText(workingAdapter.getItem(0))
        } else "0"
    }

    private fun drawText(
        canvas: Canvas, visible: Any?, radian: Double,
        measureWidth: Float,
        workingAdapter: WheelAdapter<Any>,
        listener: DrawOperatorListener
    ) {
        val firstLineY = drawOptions.firstLineY
        val secondLineY = drawOptions.secondLineY

        //获取内容文字
        val contentText: String = getContentText(visible)
        reMeasureTextSize(contentText)
        //计算开始绘制的位置
        val drawCenterContentStart = wheelContentMeasure.getCenterContentStart(
            contentText, measureWidth,
            initOptions.drawContentOffSet
        ).toFloat()
        val drawOutContentStart = wheelContentMeasure.getOutContentStart(
            contentText,
            measureWidth, initOptions.drawContentOffSet
        ).toFloat()
        val radius = wheelViewMeasure.radius.toDouble()
        val translateY = (radius - Math.cos(radian) * radius).toFloat()
        //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
        canvas.translate(0.0f, translateY)
        val scaleY = Math.abs(Math.sin(radian)).toFloat() * SCALECONTENT
        canvas.scale(1.0f, scaleY)
        val centerPaint = paintOptions.paintCenterText
        val maxTextHeight = wheelContentMeasure.getTextHeight(centerPaint).toDouble()
        val yOffset = ((wheelContentMeasure.getItemHeight(centerPaint)
            .toDouble() + maxTextHeight) / 2.0 - centerPaint.descent()).toInt()
        val halfCircumference = wheelViewMeasure.halfCircumference
        val radian_1 = (halfCircumference - wheelContentMeasure.getItemHeight(centerPaint)
            .toDouble() - maxTextHeight) / (2 * radius)
        val radian_2 = (halfCircumference - wheelContentMeasure.getItemHeight(centerPaint)
            .toDouble() + maxTextHeight) / (2 * radius)
        val radian_3 = (halfCircumference + wheelContentMeasure.getItemHeight(centerPaint)
            .toDouble() - maxTextHeight) / (2 * radius)
        val radian_4 = (halfCircumference + wheelContentMeasure.getItemHeight(centerPaint)
            .toDouble() + maxTextHeight) / (2 * radius)
        val critical_value_1 = radius * (1 - Math.cos(radian_1))
        val critical_value_2 = radius * (1 - Math.cos(radian_2))
        val critical_value_3 = radius * (1 - Math.cos(radian_3))
        val critical_value_4 = radius * (1 - Math.cos(radian_4))
        //        LogUtils.d("-->",label+"critical_value_1"+critical_value_1+"\tcritical_value_2\t"+critical_value_2);
//        LogUtils.d("-->",label+"critical_value_3"+critical_value_3+"\tcritical_value_4\t"+critical_value_4);

        if (translateY > critical_value_1 && translateY <= critical_value_2) {
            // 条目经过第一条线
            canvas.save()
            canvas.clipRect(0f, 0f, measureWidth, firstLineY.toFloat() - translateY)
            canvas.scale(1.0f, scaleY)
            val paintOuterText = paintOptions.paintOuterText
            canvas.drawText(contentText, drawOutContentStart, yOffset.toFloat(), paintOuterText)
            canvas.restore()
            canvas.save()
            canvas.clipRect(
                0f,
                firstLineY.toFloat() - translateY,
                measureWidth,
                wheelContentMeasure.getItemHeight(centerPaint)
            )
            canvas.scale(1.0f, Math.sin(Math.abs(radian)).toFloat() * 1.0f)
            canvas.drawText(contentText, drawCenterContentStart, yOffset.toFloat(), centerPaint)
            canvas.restore()
        } else if (translateY > critical_value_3 && translateY <= critical_value_4) {
            // 条目经过第二条线
            canvas.save()
            canvas.clipRect(0f, 0f, measureWidth, secondLineY.toFloat() - translateY)
            canvas.scale(1.0f, Math.sin(Math.abs(radian)).toFloat() * 1.0f)
            canvas.drawText(contentText, drawCenterContentStart, yOffset.toFloat(), centerPaint)
            canvas.restore()
            canvas.save()
            canvas.clipRect(
                0f,
                secondLineY.toFloat() - translateY,
                measureWidth,
                wheelContentMeasure.getItemHeight(centerPaint)
            )
            canvas.scale(1.0f, scaleY)
            canvas.drawText(
                contentText,
                drawOutContentStart,
                yOffset.toFloat(),
                paintOptions.paintOuterText
            )
            canvas.restore()
        } else if (translateY > critical_value_2 && translateY <= critical_value_3) {
            // 中间条目
            canvas.clipRect(0f, 0f, measureWidth, wheelContentMeasure.getItemHeight(centerPaint))
            //                    canvas.scale(1.0F, scaleY);
            //让文字居中
            val Y =
                yOffset - initOptions.drawContentOffSet //因为圆弧角换算的向下取值，导致角度稍微有点偏差，加上画笔的基线会偏上，因此需要偏移量修正一下
            canvas.drawText(contentText, drawCenterContentStart, Y, centerPaint)
            listener.lastDrawContent(visible, workingAdapter)
        } else {
            // 其他条目
            canvas.save()
            canvas.clipRect(
                0f, 0f, measureWidth, wheelContentMeasure.getItemHeight(centerPaint)
            )
            canvas.scale(1.0f, scaleY)
            canvas.drawText(
                contentText,
                drawOutContentStart,
                yOffset.toFloat(),
                paintOptions.paintOuterText
            )
            canvas.restore()
        }
        canvas.restore()
        centerPaint.textSize = paintOptions.textSize.toFloat()
    }

    /**
     * 根据文字的长度 重新设置文字的大小 让其能完全显示
     *
     */
    private fun reMeasureTextSize(contentText: String) {
        val rect = Rect()
        val paintCenterText = paintOptions.paintCenterText
        paintCenterText.getTextBounds(contentText, 0, contentText.length, rect)
        var width = rect.width()
        var size = paintOptions.textSize
        while (width > wheelViewMeasure.measureWidth) {
            size--
            //设置2条横线中间的文字大小
            paintCenterText.textSize = size.toFloat()
            paintCenterText.getTextBounds(contentText, 0, contentText.length, rect)
            width = rect.width()
        }
        //设置2条横线外面的文字大小
        paintOptions.paintOuterText.textSize = size.toFloat()
    }

    private fun drawViewContent(
        canvas: Canvas,
        itemHeightOffset: Float,
        init_radian: Double,
        temp: Float,
        itemVisibleCount: Int,
        measureWidth: Float,
        visibles: Array<Any?>,
        workingAdapter: WheelAdapter<Any>,
        listener: DrawOperatorListener
    ) {
        val radius = wheelViewMeasure.radius.toFloat()
        var counter = 0
        while (counter < itemVisibleCount) {
            canvas.save()
            // 弧长 L = itemHeight * counter - itemHeightOffset
            // 求弧度 α = L / r  (弧长/半径) [0,π]
            val tanslate_radian = (wheelContentMeasure.getItemHeight(paintOptions.paintCenterText)
                .toDouble()
                    * counter.toFloat() - itemHeightOffset) / radius
            val radian = init_radian + tanslate_radian + temp
            // 计算取值可能有细微偏差，保证负90°到90°以外的不绘制
            if (radian < Math.PI - init_radian && radian > init_radian) {

                drawText(canvas, visibles[counter], radian, measureWidth, workingAdapter, listener)
            } else {
                canvas.restore()
            }
            counter++
        }
    }

    private fun refillData(
        scrollStatus: ScrollStatus,
        workingAdapter: WheelAdapter<*>, change: Int, itemVisibleCount: Int
    ): Array<Any?> {
        val visibles = arrayOfNulls<Any>(wheelViewMeasure.itemVisibleCount)
        var counter = 0
        while (counter < itemVisibleCount) {

            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            if (initOptions.isLoop) {
                var index =
                    scrollStatus.preCurrentIndex - (itemVisibleCount / 2 - counter) //索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
                index = getLoopMappingIndex(workingAdapter, index)
                visibles[counter] = workingAdapter.getItem(index)
            } else {
                val index =
                    scrollStatus.preCurrentIndex - (itemVisibleCount / 2 - change - counter) //索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
                if (index < 0) {
                    visibles[counter] = ""
                } else if (index > workingAdapter.itemsCount - 1) {
                    visibles[counter] = ""
                } else {
                    visibles[counter] = workingAdapter.getItem(index)
                }
            }
            counter++
        }
        return visibles
    }

    //递归计算出对应的index
    private fun getLoopMappingIndex(workingAdapter: WheelAdapter<*>, index: Int): Int {
        var index = index
        if (index < 0) {
            index = index + workingAdapter.itemsCount
            index = getLoopMappingIndex(workingAdapter, index)
        } else if (index > workingAdapter.itemsCount - 1) {
            index = index - workingAdapter.itemsCount
            index = getLoopMappingIndex(workingAdapter, index)
        }
        return index
    }

    fun onDraw(
        canvas: Canvas,
        workingAdapter: WheelAdapter<Any>,
        scrollStatus: ScrollStatus,
        totalScrollY: Float,
        listener: DrawOperatorListener
    ) {
        //滚动的Y值高度除去每行Item的高度，得到滚动了多少个item，即change数
        //跟滚动流畅度有关，总滑动距离与每个item高度取余，即并不是一格格的滚动，每个item不一定滚到对应Rect里的，这个item对应格子的偏移值
        val paintCenterText = paintOptions.paintCenterText
        val itemHeightOffset =
            if (totalScrollY == 0f) -16f else totalScrollY % wheelContentMeasure.getItemHeight(
                paintCenterText
            )
        val change = (totalScrollY / wheelContentMeasure.getItemHeight(paintCenterText)).toInt()
        scrollStatus.refreshPreCurrentIndex(
            change,
            initOptions.isLoop,
            workingAdapter.itemsCount
        )
        // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
        // angle [-90°,90°]
        val radius = wheelViewMeasure.radius
        val init_radian = scrollStatus.initStartPoint / radius
        val temp = wheelContentMeasure.getItemHeight(paintCenterText) * 0.15f / radius
        val itemVisibleCount = wheelViewMeasure.itemVisibleCount

        //绘制中间两条横线
        val measureWidth = wheelViewMeasure.measureWidth.toFloat()


        // 设置数组中每个元素的值
        //可见的item数组
        val visibles = refillData(scrollStatus, workingAdapter, change, itemVisibleCount)
        drawLines(canvas, workingAdapter)
        val drawCenterContentStart = wheelContentMeasure.getCenterContentStart(
            getDrawLabel(workingAdapter),
            measureWidth, initOptions.drawContentOffSet
        )
        drawLabel(
            canvas, drawCenterContentStart,
            workingAdapter
        )
        drawViewContent(
            canvas,
            itemHeightOffset,
            init_radian,
            temp,
            itemVisibleCount,
            measureWidth,
            visibles,
            workingAdapter,
            listener
        )
    }

    interface DrawOperatorListener {
        fun lastDrawContent(drawContent: Any?, workingAdapter: WheelAdapter<*>?)
    }

    companion object {
        private const val SCALECONTENT = 0.8f //非中间文字则用此控制高度，压扁形成3d错觉
    }

    init {
        initOptions = wheelOptions.initOptions
        drawOptions = wheelOptions.drawOptions
        paintOptions = wheelOptions.paintOptions
        wheelContentMeasure = wheelOptions.wheelContentMeasure
        wheelViewMeasure = wheelOptions.wheelViewMeasure
        wheelLabelMeasure = wheelOptions.wheelLabelMeasure
    }
}