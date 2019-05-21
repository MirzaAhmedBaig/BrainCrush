package braincrush.mirza.com.braincrush.customs

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.extensions.didInTapButton

class RatingBar : LinearLayout {
    private val TAG = RatingBar::class.java.simpleName
    private var rating = 0

    private val stars by lazy {
        arrayOf(getButton(width), getButton(width), getButton(width), getButton(width), getButton(width))
    }


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
        orientation = LinearLayout.HORIZONTAL
        initRatingBar()

    }


    private fun initRatingBar() {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 1) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    stars.forEachIndexed { index, it ->
                        if (childCount == index)
                            addView(it)
                    }
                    val params = layoutParams
                    params.height = (((width - (width * 0.10)) / 5) + ((width / 5) / 2)).toInt()
                    layoutParams = params
                }
                setRatingWithoutAnimation(rating)
            }

        })
    }

    private fun getButton(width: Int): ImageButton {
        return ImageButton(context).apply {
            val subWidth = ((width - (width * 0.10)) / 5).toInt()
            layoutParams = LinearLayout.LayoutParams(subWidth, subWidth).apply {
                setMargins(0, subWidth / 4, ((width * 0.10) / 4).toInt(), 0)
                setPadding(0, 0, 0, 0)
                setOnClickListener {

                }
            }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.ic_empty_star)
            background = null
        }
    }

    fun setRatingWithoutAnimation(rating: Int) {
        this.rating = rating
        if (childCount == 0)
            return
        (0 until rating).forEach {
            with(stars[it]) {
                setImageResource(R.drawable.ic_fill_star)
            }
        }
    }

    fun setRating(rating: Int, withAnimation: Boolean = true) {
        this.rating = rating
        Log.d(TAG, "setRating : $rating Chlid Count: $childCount")
        if (childCount == 0)
            return

        var startDelay = 0L
        (0 until rating).forEach {
            with(stars[it]) {
                if (withAnimation) {
                    didInTapButton(1.0, 10.0, startDelay, 400L, onAnimationStart = {
                        setImageResource(R.drawable.ic_fill_star)
                    })
                    startDelay += 400L
                } else {
                    setImageResource(R.drawable.ic_fill_star)
                }
            }
        }

    }
}