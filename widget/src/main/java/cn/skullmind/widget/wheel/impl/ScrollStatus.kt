package cn.skullmind.widget.wheel.impl

import android.util.Log

class ScrollStatus {
    //初始化默认选中项
    var initStartPoint = 0.0 //
        protected set
    var initPosition = 0
    var preCurrentIndex = 0
    fun refreshInitStartPoint(itemHeight: Double, halfCircumference: Double, visibleCount: Int) {
        initStartPoint = (halfCircumference - itemHeight * visibleCount) / 2
    }

    fun refreshInitPosition(isLoop: Boolean, itemCounts: Int) {
        //初始化显示的item的position
        if (initPosition == -1) {
            initPosition = if (isLoop) {
                (itemCounts + 1) / 2
            } else {
                0
            }
        }
    }

    fun initPreCurrentIndex() {
        preCurrentIndex = initPosition
    }

    fun refreshPreCurrentIndex(change: Int, isLoop: Boolean, itemsCount: Int) {
        try {
            //滚动中实际的预选中的item(即经过了中间位置的item) ＝ 滑动前的位置 ＋ 滑动相对位置
            if (isLoop) {
                preCurrentIndex = initPosition + change % itemsCount
            }
        } catch (e: ArithmeticException) {
            Log.e("WheelView", "出错了！adapter.getItemsCount() == 0，联动数据不匹配")
        }
        if (!isLoop) { //不循环的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0
            }
            if (preCurrentIndex > itemsCount - 1) {
                preCurrentIndex = itemsCount - 1
            }
        } else { //循环
            if (preCurrentIndex < 0) { //举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
                preCurrentIndex = itemsCount + preCurrentIndex
            }
            if (preCurrentIndex > itemsCount - 1) { //同理上面,自己脑补一下
                preCurrentIndex = preCurrentIndex - itemsCount
            }
        }
    }
}