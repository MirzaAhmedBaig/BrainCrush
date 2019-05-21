package braincrush.mirza.com.braincrush.utils

/**
 * Created by bala on 25-11-2017.
 */
internal class MyBounceInterpolator(amplitude: Double, frequency: Double) : android.view.animation.Interpolator {
    private var mAmplitude = 1.0
    private var mFrequency = 10.0

    init {
        mAmplitude = amplitude
        mFrequency = frequency
    }

    override fun getInterpolation(time: Float): Float {
        return (-1.0 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1).toFloat()
    }
}