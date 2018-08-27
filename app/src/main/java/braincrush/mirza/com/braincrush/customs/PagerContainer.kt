package braincrush.mirza.com.braincrush.customs

/**
 * Created by AHMED on 02-07-2017.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * PagerContainer: A layout that displays a ViewPager with its children that are outside
 * the typical pager bounds.
 */
class PagerContainer : FrameLayout, ViewPager.OnPageChangeListener {

    var viewPager: ViewPager? = null
        private set
    internal var mNeedsRedraw = false

    private val mCenter = Point()
    private val mInitialTouch = Point()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        //Disable clipping of children so non-selected pages are visible
        clipChildren = false

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    @SuppressLint("MissingSuperCall")
    override fun onFinishInflate() {
        try {
            viewPager = getChildAt(0) as ViewPager
            viewPager?.addOnPageChangeListener(this)
        } catch (e: Exception) {
            throw IllegalStateException("The root child of PagerContainer must be a ViewPager")
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mCenter.x = w / 2
        mCenter.y = h / 2
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialTouch.x = ev.x.toInt()
                mInitialTouch.y = ev.y.toInt()
                ev.offsetLocation((mCenter.x - mInitialTouch.x).toFloat(), (mCenter.y - mInitialTouch.y).toFloat())
            }
            else -> ev.offsetLocation((mCenter.x - mInitialTouch.x).toFloat(), (mCenter.y - mInitialTouch.y).toFloat())
        }

        return viewPager!!.dispatchTouchEvent(ev)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        //        level.setText("hskdhfkjhd");
        if (mNeedsRedraw) invalidate()
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {
        mNeedsRedraw = state != ViewPager.SCROLL_STATE_IDLE
    }
}
