package cn.skullmind.widget.wheel.impl

import android.graphics.Paint
import cn.skullmind.widget.wheel.IPickerViewData
import cn.skullmind.widget.wheel.WheelItem


object WheelUtils {
    fun getContentText(item: Any?): String {
        return when (item) {
            null -> {
                ""
            }
            is IPickerViewData -> {
                item.pickerViewText
            }
            is Int -> {
                //如果为整形则最少保留两位数.
                item.toString()
            }
            is WheelItem -> {
                item.text
            }
            else -> item.toString()
        }
    }

    fun getTextWidth(paint: Paint, str: String?): Int { //计算文字宽度
        var iRet = 0
        if (str != null && str.length > 0) {
            val len = str.length
            val widths = FloatArray(len)
            paint.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }
}