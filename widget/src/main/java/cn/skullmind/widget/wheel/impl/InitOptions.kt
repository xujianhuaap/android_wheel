package cn.skullmind.widget.wheel.impl

import android.view.Gravity
import cn.skullmind.widget.wheel.WheelView

class InitOptions(var lineSpacingMultiplier: Float, density: Float) {
    var drawContentOffSet //偏移量
            = 0f
        private set
    var gravity: Int = Gravity.CENTER
    var isCenterLabel = true
    var isStartCenter = false
    var isOptions = false
    var isLoop = false
    var dividerType //分隔线类型
            : WheelView.DividerType? = null
    var label //附加单位
            : String? = null

    /**
     * 判断间距是否在1.0-2.0之间
     */
    private fun judgeLineSpae() {
        if (lineSpacingMultiplier < 1.2f) {
            lineSpacingMultiplier = 1.2f
        } else if (lineSpacingMultiplier > 4f) {
            lineSpacingMultiplier = 4.0f
        }
    }

    private fun refreshDrawContentOffSet(density: Float) {
        var CENTERCONTENTOFFSET1 = 0f
        if (density < 1) { //根据密度不同进行适配
            CENTERCONTENTOFFSET1 = 2.4f
        } else if (1 <= density && density < 2) {
            CENTERCONTENTOFFSET1 = 3.6f
        } else if (2 <= density && density < 3) {
            CENTERCONTENTOFFSET1 = 6.0f
        } else if (density >= 3) {
            CENTERCONTENTOFFSET1 = density * 2.5f
        }
        drawContentOffSet = CENTERCONTENTOFFSET1
    }

    init {
        judgeLineSpae()
        refreshDrawContentOffSet(density)
    }
}