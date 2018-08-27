package braincrush.mirza.com.braincrush.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.adapters.LevelsCarouselAdapter
import braincrush.mirza.com.braincrush.fragments.AlertDialogFragment
import braincrush.mirza.com.braincrush.interfaces.LevelSelectionListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import com.gtomato.android.ui.transformer.FlatMerryGoRoundTransformer
import com.gtomato.android.ui.widget.CarouselView
import kotlinx.android.synthetic.main.activity_single_player.*


class SinglePlayerActivity : AppCompatActivity(), LevelSelectionListener {

    private val TAG = SinglePlayerActivity::class.java.simpleName

    private val levelsArray = ArrayList<Class<*>>()
    private val levelsDataList = ArrayList<ScoreModel>()

    private val messageDialog by lazy {
        AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", "Level Locked!")
                putString("msg", "You don't have enough stars to play this level")
                putInt("type", AlertDialogFragment.SIMPLE_TYPE)
            }
            isCancelable = false
        }

    }

    private var levelNumber = 0
    private var isCurrentLevelLocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_single_player)

        fillLevelsArray()
        setListeners()
        setViewPager()
    }

    private fun setListeners() {
        next_level.setOnClickListener {
            Log.d(TAG, "Child Count : ${carousel.childCount}")
            if (carousel.currentPosition != levelsDataList.lastIndex) {
                carousel.smoothScrollToPosition(carousel.currentPosition + 1)
            }
        }
        previous_level.setOnClickListener {
            if (carousel.currentPosition != 0) {
                carousel.smoothScrollToPosition(carousel.currentPosition - 1)
            }
        }
        start_button.setOnClickListener {
            if (isCurrentLevelLocked) {
                messageDialog.show(supportFragmentManager, "locked_level_alert")
            } else {
                startActivity(Intent(this, levelsArray[levelNumber]))
            }
        }
    }

    private fun setViewPager() {
        for (i in 0..5) {
            levelsDataList.add(ScoreModel(i + 1, 0f, R.drawable.l1, true))
        }
        with(carousel) {
            transformer = FlatMerryGoRoundTransformer()
            adapter = LevelsCarouselAdapter(levelsDataList).apply {
                setLevelSelectionListener(this@SinglePlayerActivity)
            }
            setOnItemSelectedListener(object : CarouselView.OnItemSelectedListener {
                override fun onItemSelected(carouselView: CarouselView, position: Int, adapterPosition: Int, adapter: RecyclerView.Adapter<*>) {
                    levelNumber = position
                    isCurrentLevelLocked = levelsDataList[position].isLocked
                    level_text.text = "LEVEL ${levelNumber + 1}"
                }

                override fun onItemDeselected(carouselView: CarouselView, position: Int, adapterPosition: Int, adapter: RecyclerView.Adapter<*>) {

                }
            })

        }


    }

    private fun fillLevelsArray() {
        for (i in 0..5) {
            levelsArray.add(LevelOneActivity::class.java)
        }
    }

    override fun onLevelSelected(levelNumber: Int, isLocked: Boolean) {
        if (isLocked) {
            messageDialog.show(supportFragmentManager, "locked_level_alert")
        } else {
            startActivity(Intent(this, levelsArray[levelNumber]))
        }
    }

}