package cn.skullmind.widget.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import cn.skullmind.widget.R
import cn.skullmind.widget.wheel.impl.*


/**
 * 3d滚轮控件
 */
class WheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private val handler: MessageHandler
    var mStatus = STATUS.IDLE
    lateinit var onItemSelectedListener: OnItemSelectedListener
    private val drawOperatorListener: DrawOperator.DrawOperatorListener
    private lateinit var wheelOptions: WheelOptions
    private lateinit var scrollOperator: ScrollOperator
    private val touchOperator: TouchOperator
    private lateinit var drawOperator: DrawOperator

    init {
        handler = MessageHandler(this)

        val dm: DisplayMetrics = resources.displayMetrics
        val density: Float = dm.density // 屏幕密度（0.75/1.0/1.5/2.0/3.0）
        var measuredHeight = 0
        var dividerColor = 0
        var textColorCenter = 0
        var textColorOut = 0
        var textSize = resources.getDimensionPixelSize(R.dimen.pickerview_textsize) //默认大小
        val textSizeLabel = resources.getDimensionPixelSize(R.dimen.pickerview_textsize2) //默认大小
        var initGravity: Int = Gravity.CENTER
        // 条目间距倍数
        var lineSpacingMultiplier = 1.83f
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.pickerview, 0, 0)
            initGravity = a.getInt(R.styleable.pickerview_pickerview_gravity, Gravity.CENTER)
            textColorOut = a.getColor(R.styleable.pickerview_pickerview_textColorOut, textColorOut)
            textColorCenter =
                a.getColor(R.styleable.pickerview_pickerview_textColorCenter, textColorCenter)
            dividerColor = a.getColor(R.styleable.pickerview_pickerview_dividerColor, dividerColor)
            textSize =
                a.getDimensionPixelOffset(R.styleable.pickerview_pickerview_textSize, textSize)
            lineSpacingMultiplier = a.getFloat(
                R.styleable.pickerview_pickerview_lineSpacingMultiplier,
                lineSpacingMultiplier
            )
            measuredHeight =
                a.getDimensionPixelSize(R.styleable.pickerview_pickerview_view_measuredHeight, 0)
            a.recycle() //回收内存
        }
        val wheelViewMeasure = WheelViewMeasure(measuredHeight)
        val initOptions = InitOptions(lineSpacingMultiplier, density)
        initOptions.gravity = initGravity
        val builder: PaintOptionsBuilder = PaintOptionsBuilder()
            .setTextColorCenter(textColorCenter)
            .setTextColorOut(textColorOut)
            .setDividerColor(dividerColor)
            .setTextSize(textSize)
            .setTextSizeLabel(textSizeLabel)
        val paintOptions: PaintOptions = builder.createContentPaint()
        touchOperator = TouchOperator(getContext(), LoopViewGestureListener(this))
        touchOperator.totalScrollY = 0f
        val selectListener: SelectItemState.SelectListener = object:SelectItemState.SelectListener{
            override fun startExecutePending() {
                mStatus = STATUS.WORKING
            }

            override fun endExecutePending() {
                sendMessage(MessageHandler.WHAT_ITEM_SELECTED)
            }

            override fun updatedWorkingAdapter() {
                if (mStatus != STATUS.IDLE) {
                    scrollOperator.cancelFuture()
                    handler.cleanMessages()
                }
                wheelOptions.reset()
                touchOperator.reset()
                postDelayed({
                    wheelOptions.remeasure(height)
                    invalidate()
                }, 50)
            }
        }
        scrollOperator = ScrollOperator(object : ScrollOperator.ScrollOperatorListener {
            override fun getOffSet(action: ACTION): Int {
                touchOperator.refreshOffSet(
                    action, wheelOptions.paintOptions,
                    wheelOptions.wheelContentMeasure
                )
                return touchOperator.offset
            }

            override fun scrollBy(velocityY: Float) {
                //滚动惯性的实现
                mStatus = STATUS.WORKING
            }
        })
        wheelOptions = WheelOptions(
            paintOptions,
            initOptions,
            wheelViewMeasure,
            selectListener
        )
        wheelOptions.setLoop(true)
        wheelOptions.setInitPosition(-1)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        drawOperatorListener = object :DrawOperator.DrawOperatorListener{
            override fun lastDrawContent(drawContent: Any?, workingAdapter: WheelAdapter<*>) {
                val selectedItem = workingAdapter.indexOf(drawContent)
                wheelOptions.setSelectedItem(selectedItem)
                wheelOptions.setValidSelectedItem(selectedItem)
                wheelOptions.setValidAdapter(workingAdapter)
            }
        }
    }

    fun smoothScroll() {
        scrollOperator.smoothScroll(ACTION.FLING, object : SmoothScrollTimerTask.Listener {
            override fun endScroll() {
                scrollOperator.cancelFuture()
                //非循环纠正
                if (totalScrollY < 0) {
                    totalScrollY = totalScrollY - 0.15f * wheelOptions.itemHeight
                }
                sendMessage(MessageHandler.WHAT_ITEM_SELECTED)
            }

            override fun scrolling(realTotalOffset: Int, realOffset: Int): Boolean {
                totalScrollY = totalScrollY + realOffset
                // 边界处理。
                //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
                if (!wheelOptions.initOptions.isLoop) {
                    val itemHeight: Float = wheelOptions.itemHeight
                    val top =
                        -wheelOptions.scrollStatus.initPosition as Float * itemHeight
                    val bottom = (wheelOptions.itemsCount - 1 - wheelOptions.scrollStatus
                        .initPosition) as Float * itemHeight
                    if (totalScrollY <= top || totalScrollY >= bottom) {
                        totalScrollY = totalScrollY - realOffset
                        scrollOperator.cancelFuture()
                        sendMessage(MessageHandler.WHAT_ITEM_SELECTED)
                        return true
                    }
                }
                sendMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
                return false
            }

        })
    }

    fun scrollBy(velocityY: Float) {
        scrollOperator.scrollBy(velocityY, object : InertiaTimerTask.Listener {
            override fun executeSmoothScroll() {
                scrollOperator.cancelFuture()
                sendMessage(MessageHandler.WHAT_SMOOTH_SCROLL)
            }

            override fun refreshViewAfterLongScroll() {
                sendMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
            }

            override fun refreshTotalScrollY(i: Int, initPendingScrollValue: Float): Float {
                var initPendingScrollValue = initPendingScrollValue
                totalScrollY = totalScrollY - i
                if (!wheelOptions.initOptions.isLoop) {
                    val itemHeight: Float = wheelOptions.itemHeight
                    var top: Float = -wheelOptions.scrollStatus.initPosition * itemHeight
                    var bottom: Float =
                        (wheelOptions.itemsCount- 1 - wheelOptions.scrollStatus
                            .initPosition) * itemHeight
                    if (totalScrollY - itemHeight * 0.25 < top) {
                        top = totalScrollY + i
                    } else if (totalScrollY + itemHeight * 0.25 > bottom) {
                        bottom = totalScrollY + i
                    }
                    if (totalScrollY <= top) {
                        initPendingScrollValue = 40f
                        totalScrollY = top
                    } else if (totalScrollY >= bottom) {
                        totalScrollY = bottom
                        initPendingScrollValue = -40f
                    }
                }
                if (initPendingScrollValue < 0.0f) {
                    initPendingScrollValue += 20f
                } else {
                    initPendingScrollValue -= 20f
                }
                return initPendingScrollValue
            }
        })
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    fun setCyclic(cyclic: Boolean) {
        wheelOptions.setLoop(cyclic)
    }

    fun setTypeface(font: Typeface) {
        wheelOptions.setTypeface(font)
    }

    fun setTextSize(size: Float) {
        wheelOptions.setTextSize(this.context, size)
    }

    @JvmName("setOnItemSelectedListener1")
    fun setOnItemSelectedListener(OnItemSelectedListener: OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener
    }

    @get:Synchronized
    val selectItemContent: Any?
        get() = wheelOptions.selectItemContent

    fun <T> setPendingAdapter(adapter: WheelAdapter<T>) {
        wheelOptions.setPendingAdapter(adapter, mStatus)
    }

    fun executePendingAdapter() {
        wheelOptions.executePendingAdapter()
    }

    @get:Synchronized
    var currentItem: Int
        get() = wheelOptions.selectItemState.validSelectedItem
        set(currentItem) {
            wheelOptions.setInitPosition(currentItem)
            touchOperator.totalScrollY = 0f
            invalidate()
        }

    fun onItemSelected() {
        if (onItemSelectedListener != null) {
            scrollOperator.cancelFuture()
            handler.cleanMessages()
            mStatus = STATUS.IDLE
            postDelayed(
                { onItemSelectedListener.onItemSelected(currentItem) },
                200L
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        wheelOptions.remeasureIfNeed(height)
        val workingAdapter: WheelAdapter<Any> = wheelOptions.wheelAdapter ?: return
        if (drawOperator == null) {
            drawOperator = DrawOperator(wheelOptions)
        }
        drawOperator.onDraw(
            canvas,
            workingAdapter,
            wheelOptions.scrollStatus,
            totalScrollY,
            drawOperatorListener
        )
    }

    private var totalScrollY: Float
        private get() = touchOperator.totalScrollY
        set(totalScrollY) {
            touchOperator.totalScrollY = totalScrollY
        }

    fun setStartCenter(startCenter: Boolean) {
        wheelOptions.setStartCenter(startCenter)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        wheelOptions.remeasureWhenChange(widthMeasureSpec, height)
        val wheelViewMeasure: WheelViewMeasure = wheelOptions.wheelViewMeasure
        setMeasuredDimension(
            wheelViewMeasure.measureWidth,
            wheelViewMeasure.measuredHeight
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mStatus = STATUS.WORKING
        if (event.getAction() == MotionEvent.ACTION_DOWN) scrollOperator.cancelFuture()
        touchOperator.handleTouchEvent(event, wheelOptions)
        invalidate()
        return true
    }

    /**
     * 附加在右边的单位字符串
     *
     * @param label 单位
     */
    fun setLabel(label: String?) {
        wheelOptions.setLabel(label)
    }

    fun setCenterLabel(isCenterLabel: Boolean?) {
        wheelOptions.isCenterLabel(isCenterLabel)
    }

    fun setGravity(gravity: Int) {
        wheelOptions.setGravity(gravity)
    }

    private fun sendMessage(what: Int) {
        if (handler != null) {
            handler.sendEmptyMessage(what)
        }
    }

    fun setIsOptions(options: Boolean) {
        wheelOptions.setIsOptions(options)
    }

    fun setTextColorOut(textColorOut: Int) {
        wheelOptions.setTextColorOut(textColorOut)
    }

    fun setTextColorCenter(textColorCenter: Int) {
        wheelOptions.setTextColorCenter(textColorCenter)
    }

    fun setDividerColor(dividerColor: Int) {
        wheelOptions.setDividerColor(dividerColor)
    }

    fun setDividerType(dividerType: DividerType) {
        wheelOptions.setDividerType(dividerType)
    }

    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float) {
        wheelOptions.setLineSpacingMultiplier(lineSpacingMultiplier)
    }

    enum class ACTION {
        // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DAGGLE
    }

    enum class DividerType {
        // 分隔线类型
        FILL, WRAP
    }

    @Synchronized
    fun setSelectedItem(selectedItem: Int) {
        wheelOptions.setSelectedItem(selectedItem)
    }

    enum class STATUS {
        IDLE, WORKING
    }


}