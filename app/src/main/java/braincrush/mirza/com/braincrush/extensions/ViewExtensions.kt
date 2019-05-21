package braincrush.mirza.com.braincrush.extensions

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.utils.MyBounceInterpolator

fun View.didInTapButton(amplitude: Double = 0.2, frequency: Double = 20.0, startDelay: Long = 0L, duration: Long = 300L, onAnimationStart: () -> Unit = {}, onAnimationEnd: () -> Unit = {}) {
    isClickable = false
    val bounceInterpolator = MyBounceInterpolator(amplitude, frequency)
    val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_fade_in).apply {
        interpolator = bounceInterpolator
        this.duration = duration
        this.startOffset = startDelay
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                onAnimationStart.invoke()
                this@didInTapButton.visibility = View.VISIBLE

            }

            override fun onAnimationEnd(animation: Animation) {
                onAnimationEnd.invoke()
                this@didInTapButton.isClickable = true
            }
        })
    }
    startAnimation(myAnim)
}


fun View.animateHide(duration: Long = 500, offset: Long = 0, fromAlpha: Float = 1f, toAlpha: Float = 0f, onAnimationEnd: () -> Unit = {}) {
    val anim = AlphaAnimation(fromAlpha, toAlpha)
    anim.duration = duration
    anim.startOffset = offset
    animation = anim
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {}

        override fun onAnimationStart(p0: Animation?) {}

        override fun onAnimationEnd(animation: Animation) {
            visibility = View.GONE
            onAnimationEnd.invoke()
        }
    })
    startAnimation(animation)
}

fun View.animateShow(onAnimationEnd: () -> Unit = {}, duration: Long = 400, offset: Long = 0, fromAlpha: Float = 0f, toAlpha: Float = 1f) {
    isClickable = false
    val anim = AlphaAnimation(fromAlpha, toAlpha)
    anim.duration = duration
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
        }

        override fun onAnimationStart(p0: Animation?) {
            this@animateShow.visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animation) {
            isClickable = true
            onAnimationEnd.invoke()
        }
    })
    anim.startOffset = offset
    this.startAnimation(anim)
}

fun Button.animateText() {
    ObjectAnimator.ofInt(this, "textColor", Color.rgb(76, 175, 80), Color.TRANSPARENT).apply {
        duration = 800
        setEvaluator(ArgbEvaluator())
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }.start()

}

