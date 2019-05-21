package braincrush.mirza.com.braincrush.customs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import braincrush.mirza.com.braincrush.interfaces.OnDrawCompleteListener

class GameTriangle : View {
    private val TAG = GameTriangle::class.java.simpleName
    private var mPaint: Paint? = null
    private var textPaint: Paint? = null
    private var rect1: RectF? = null
    private var rect2: RectF? = null
    private var rect3: RectF? = null
    private val textRect = Rect()
    private var numberOne = 0
    private var numberTwo = 0
    private var numberThree = 0
    private var positionList = ArrayList<Pair<Float, Float>>()
    private var onDrawCompleteListener: OnDrawCompleteListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {

        mPaint = Paint()
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.BLACK
        mPaint!!.strokeWidth = 5f
        mPaint!!.strokeCap = Paint.Cap.ROUND

        textPaint = Paint()
        textPaint!!.color = Color.BLACK
        textPaint!!.style = Paint.Style.FILL
        textPaint!!.textSize = 80f

        rect1 = RectF()
        rect2 = RectF()
        rect3 = RectF()
    }

    fun setNumbers(numberOne: Int, numberTwo: Int, numberThree: Int) {
        this.numberOne = numberOne
        this.numberTwo = numberTwo
        this.numberThree = numberThree
        invalidate()

    }

    fun setTextColor(color: Int) {
        textPaint?.color = color
    }

    fun setBorderColor(color: Int) {
        mPaint?.color = color
    }

    fun setListener(onDrawCompleteListener: OnDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val offset = Math.min(width, height)


        textPaint!!.textSize = offset * 0.08f
        mPaint!!.strokeWidth = offset * 0.006f

        val radius = (offset / 4).toFloat() / 2


        val x1 = (offset / 2).toFloat()
        val y1 = radius + mPaint!!.strokeWidth
        val y2 = height - radius - mPaint!!.strokeWidth
        val x2 = offset - radius - mPaint!!.strokeWidth

        //mid Points
        val mid_x_1 = (x1 + y1) / 2
        val mid_y_1 = (y1 + y2) / 2

        val mid_x_2 = (x1 + x2) / 2
        val mid_y_2 = (y1 + y2) / 2

        val mid_x_3 = (y1 + x2) / 2
        val mid_y_3 = y2

        canvas?.drawCircle(x1, y1, radius, mPaint)
        canvas?.drawCircle(y1, y2, radius, mPaint)
        canvas?.drawCircle(x2, y2, radius, mPaint)

        positionList.clear()
        positionList.add(Pair(x1, y1))
        positionList.add(Pair(y1, y2))
        positionList.add(Pair(x2, y2))

        rect1?.apply {
            left = mid_x_1 - radius
            top = mid_y_1 - radius / 2
            right = mid_x_1 + radius
            bottom = mid_y_1 + radius / 2
        }
        rect2?.apply {
            left = mid_x_2 - radius
            top = mid_y_2 - radius / 2
            right = mid_x_2 + radius
            bottom = mid_y_2 + radius / 2
        }
        rect3?.apply {
            left = mid_x_3 - radius
            top = mid_y_3 - radius / 2
            right = mid_x_3 + radius
            bottom = mid_y_3 + radius / 2
        }
        canvas?.drawRect(rect1, mPaint)
        canvas?.drawRect(rect2, mPaint)
        canvas?.drawRect(rect3, mPaint)

        val leftLineDistance = Math.sqrt(((y1 - x1) * (y1 - x1) + (y2 - y1) * (y2 - y1)).toDouble())
        val rightLineDistance = Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toDouble())
        val midPointDistance = leftLineDistance / 2

        val dl = leftLineDistance - radius
        val dr = rightLineDistance - radius


        val xl1 = x1 - (radius * (x1 - y1) / leftLineDistance).toFloat()
        val yl1 = y1 - (radius * (y1 - y2) / leftLineDistance).toFloat()

        val xlm1 = x1 - ((midPointDistance - radius / 2) * (x1 - mid_x_1) / midPointDistance).toFloat()
        val xlm2 = y1 - ((midPointDistance - radius / 2) * (y1 - mid_x_1) / midPointDistance).toFloat()

        val xl2 = x1 - (dl * (x1 - y1) / leftLineDistance).toFloat()
        val yl2 = y1 - (dl * (y1 - y2) / leftLineDistance).toFloat()

        val xr1 = x1 - (radius * (x1 - x2) / rightLineDistance).toFloat()
        val yr1 = y1 - (radius * (y1 - y2) / rightLineDistance).toFloat()

        val xrm1 = x1 - ((midPointDistance - radius / 2) * (x1 - mid_x_2) / midPointDistance).toFloat()
        val xrm2 = x2 - ((midPointDistance - radius / 2) * (x2 - mid_x_2) / midPointDistance).toFloat()

        val xr2 = x1 - (dr * (x1 - x2) / rightLineDistance).toFloat()
        val yr2 = y1 - (dr * (y1 - y2) / rightLineDistance).toFloat()


        //FirstLine
        canvas?.drawLine(xl1, yl1, xlm1, rect1!!.top, mPaint)
        canvas?.drawLine(xlm2, rect1!!.bottom, xl2, yl2, mPaint)

        //secondLine
        canvas?.drawLine(xr1, yr1, xrm1, rect2!!.top, mPaint)
        canvas?.drawLine(xrm2, rect2!!.bottom, xr2, yr2, mPaint)

        //thirdLine
        canvas?.drawLine(y1 + radius, y2, rect3!!.left, y2, mPaint)
        canvas?.drawLine(rect3!!.right, y2, x2 - radius, y2, mPaint)

        textPaint!!.getTextBounds(numberOne.toString(), 0, numberOne.toString().length, textRect)
        var xPos = (rect1!!.width() / 2) - textRect.width() / 2
        val yPos = ((rect1!!.height() / 2) - ((textPaint!!.descent() + textPaint!!.ascent()) / 2))


        canvas?.drawText(numberOne.toString(), rect1!!.left + xPos, rect1!!.top + yPos, textPaint)

        textPaint!!.getTextBounds(numberTwo.toString(), 0, numberTwo.toString().length, textRect)
        xPos = (rect2!!.width() / 2) - textRect.width() / 2

        canvas?.drawText(numberTwo.toString(), rect2!!.left + xPos, rect2!!.top + yPos, textPaint)

        textPaint!!.getTextBounds(numberThree.toString(), 0, numberThree.toString().length, textRect)
        xPos = (rect3!!.width() / 2) - textRect.width() / 2

        canvas?.drawText(numberThree.toString(), rect3!!.left + xPos, rect3!!.top + yPos, textPaint)

        onDrawCompleteListener?.onDrawCompletes(positionList)
        Log.d(TAG, "OnDraw is done")


    }


}