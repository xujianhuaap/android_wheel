package cn.skullmind.widget.wheel.impl

class DrawOptions(
    private val wheelViewMeasure: WheelViewMeasure,
    private val wheelContentMeasure: WheelContentMeasure,
    private val paintOptions: PaintOptions
) {
    // 第一条线Y坐标值
    var firstLineY = 0.0
        private set

    //第二条线Y坐标
    var secondLineY = 0.0
        private set

    //中间label绘制的Y坐标
    var centerY = 0.0
        private set

    fun refreshLines() {
        val radius = wheelViewMeasure.radius
        val halfCircumference = wheelViewMeasure.halfCircumference
        val itemHeight = wheelContentMeasure.getItemHeight(paintOptions.paintCenterText)
        refreshFirstLine(radius, halfCircumference, itemHeight)
        refreshSecondLineY(radius, halfCircumference, itemHeight)
        refreshCenterY()
    }

    private fun refreshFirstLine(radius: Int, halfCircumference: Double, itemHeight: Float) {
        //求出半径

        //计算两条横线 和 选中项画笔的基线Y位置
        val radianFirstLine = (halfCircumference - itemHeight.toDouble()) / (radius * 2)
        firstLineY = radius * (1 - Math.cos(radianFirstLine))
    }

    private fun refreshSecondLineY(radius: Int, halfCircumference: Double, itemHeight: Float) {
        val radianSecondLine = (halfCircumference + itemHeight.toDouble()) / (radius * 2)
        secondLineY = radius * (1 - Math.cos(radianSecondLine))
    }

    private fun refreshCenterY() {
        centerY = (firstLineY + secondLineY) / 2.0f
    }
}