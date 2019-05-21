package braincrush.mirza.com.braincrush.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.customs.ShadowLayerClass
import braincrush.mirza.com.braincrush.extensions.didInTapButton
import braincrush.mirza.com.braincrush.fragments.AlertDialogFragment
import braincrush.mirza.com.braincrush.fragments.GameInfoDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var displayMetrics: DisplayMetrics? = null
    private val gameInfoDailog by lazy {
        GameInfoDialog()
    }
    private val messageDialog by lazy {
        AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", "App in progress!")
                putString("msg", "Thanks for testing my app. I will soon add this functionality.")
                putInt("type", AlertDialogFragment.SIMPLE_TYPE)
            }
            isCancelable = false
        }

    }
    private var width: Int = 0
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        initDisplayMatrix()
        setUpListener()
        applyShadows()

    }

    private fun setUpListener() {
        single_player.setOnClickListener(this)
        multi_player.setOnClickListener(this)
        settings_btn.setOnClickListener(this)
        like_btn.setOnClickListener(this)
        share_btn.setOnClickListener(this)
        info_btn.setOnClickListener(this)
    }

    private fun initDisplayMatrix() {
        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics!!.heightPixels
        width = displayMetrics!!.widthPixels
    }

    private fun singlePlay() {
//        text_one.didInTapButton()
        val intent = Intent(this, SinglePlayerActivity::class.java)
        startActivity(intent)

    }

    private fun multiPlay() {
//        text_two.didInTapButton()
        messageDialog.show(supportFragmentManager, "like_alert")
    }

    private fun settings() {
//        text_three.didInTapButton()
        messageDialog.show(supportFragmentManager, "like_alert")
    }

    private fun share() {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        val shareBodyText = "I love The Brain Twist App...! \nThis is the best game to boost your brain ability.\nDownload it from here\nhttps://drive.google.com/open?id=0By5B0_-J_O5tWTY3NkFKVkx5ZHM"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "The Brain Gain App")
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(intent, "Choose sharing method"))
    }

    private fun applyShadows() {
        val source = ResourcesCompat.getDrawable(resources, R.drawable.ic_button_back, null)
        val mask = ResourcesCompat.getDrawable(resources, R.drawable.ic_button_back, null)
        val colors = intArrayOf(Color.DKGRAY, Color.DKGRAY)
        val shadow = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        val shadowLayer = ShadowLayerClass(this, source!!, mask!!, shadow, 10f)
        single_player.background = ShapeDrawable(shadowLayer)
        multi_player.background = ShapeDrawable(shadowLayer)
        settings_btn.background = ShapeDrawable(shadowLayer)
    }

    override fun onClick(view: View) {
        view.didInTapButton {
            when (view.id) {
                R.id.single_player -> singlePlay()
                R.id.multi_player -> multiPlay()
                R.id.settings_btn -> settings()
                R.id.like_btn -> messageDialog.show(supportFragmentManager, "like_alert")
                R.id.share_btn -> share()
                R.id.info_btn -> gameInfoDailog.show(supportFragmentManager, "info_alert")
            }
        }

    }

}