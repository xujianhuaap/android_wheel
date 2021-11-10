package cn.skullmind.widget.wheel.impl

import cn.skullmind.widget.wheel.WheelAdapter
import cn.skullmind.widget.wheel.WheelView


class SelectItemState(private val listener: SelectListener) {
    private var validAdapter: WheelAdapter<*>? = null
    private  var workingAdapter: WheelAdapter<*>? =null
    private  var pendingAdapter: WheelAdapter<*>? = null

    //选中的Item是第几个
    protected var selectedItem = 0
    var validSelectedItem = 0
    fun getWorkingAdapter(): WheelAdapter<*>? {
        return workingAdapter
    }

    @JvmName("setSelectedItem1")
    fun setSelectedItem(selectedItem: Int) {
        this.selectedItem = selectedItem
    }

    fun setValidAdapter(validAdapter: WheelAdapter<*>) {
        this.validAdapter = validAdapter
    }

    @get:Synchronized
    val selectItemContent: Any?
        get() = validAdapter?.getItem(validSelectedItem)?:""

    fun setPendingAdapter(adapter: WheelAdapter<*>, status: WheelView.STATUS) {
        pendingAdapter = adapter
        if (status === WheelView.STATUS.IDLE) executeAdapter()
    }

    fun executePendingAdapter() {
        pendingAdapter?.run {
            listener.startExecutePending()
            executeAdapter()
            listener.endExecutePending()
        }
    }

    private fun executeAdapter() {
        workingAdapter = pendingAdapter
        pendingAdapter = null
        listener.updatedWorkingAdapter()
    }

    interface SelectListener {
        fun startExecutePending()
        fun endExecutePending()
        fun updatedWorkingAdapter()
    }
}