package braincrush.mirza.com.braincrush.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.activities.Levels.*
import braincrush.mirza.com.braincrush.adapters.LevelsCarouselAdapter
import braincrush.mirza.com.braincrush.extensions.didInTapButton
import braincrush.mirza.com.braincrush.fragments.AlertDialogFragment
import braincrush.mirza.com.braincrush.interfaces.LevelSelectionListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import com.gtomato.android.ui.transformer.FlatMerryGoRoundTransformer
import com.gtomato.android.ui.widget.CarouselView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_single_player.*


class SinglePlayerActivity : AppCompatActivity(), LevelSelectionListener {

    private val TAG = SinglePlayerActivity::class.java.simpleName

    private val levelsArray = ArrayList<Class<*>>()
    private var levelsDataList = ArrayList<ScoreModel>()

    private var currentLevel = -1


    private val thumbnails = arrayOf(R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1, R.drawable.l1)


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

    private var layoutId = R.layout.activity_single_player
    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("layoutId", layoutId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layoutId", R.layout.activity_main)
        }
        setContentView(layoutId)
//        setContentView(R.layout.activity_single_player)

        fillLevelsArray()
        setListeners()

    }

    override fun onResume() {
        super.onResume()
        setAppsLevels()
        setViewPager()
    }

    private fun setListeners() {
        next_level.setOnClickListener {
            it.didInTapButton {
                if (carousel.currentPosition != levelsDataList.lastIndex) {
                    carousel.smoothScrollToPosition(carousel.currentPosition + 1)
                }
            }
        }
        previous_level.setOnClickListener {
            it.didInTapButton {
                if (carousel.currentPosition != 0) {
                    carousel.smoothScrollToPosition(carousel.currentPosition - 1)
                }
            }
        }
        start_button.setOnClickListener {
            start_button.didInTapButton {
                if (isCurrentLevelLocked) {
                    if (!messageDialog.isAdded)
                        messageDialog.show(supportFragmentManager, "locked_level_alert")
                } else {
                    startActivity(Intent(this, levelsArray[levelNumber]))
                }
            }

        }
    }

    private fun setViewPager() {

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
                    Log.d(TAG, "Position of carousel View : $position")
                }

                override fun onItemDeselected(carouselView: CarouselView, position: Int, adapterPosition: Int, adapter: RecyclerView.Adapter<*>) {

                }
            })
            smoothScrollToPosition(currentLevel)
            levelNumber = currentLevel
        }
        Handler().postDelayed({
            runOnUiThread {
                level_text.text = "LEVEL ${currentLevel + 1}"
            }
        }, 500)

    }

    private fun fillLevelsArray() {
        levelsArray.add(LevelOneActivity::class.java)
        levelsArray.add(LevelTwoActivity::class.java)
        levelsArray.add(LevelThreeActivity::class.java)
        levelsArray.add(LevelFourActivity::class.java)
        levelsArray.add(LevelFiveActivity::class.java)
        levelsArray.add(LevelSevenActivity::class.java)
    }

    override fun onLevelSelected(levelNumber: Int, isLocked: Boolean) {
        if (isLocked) {
            if (!messageDialog.isAdded)
                messageDialog.show(supportFragmentManager, "locked_level_alert")
        } else {
            startActivity(Intent(this, levelsArray[levelNumber]))
        }
    }

    private fun setAppsLevels() {
        currentLevel = -1
        levelsDataList = ArrayList()
        val realm = Realm.getDefaultInstance()
        val realmListForLevels = realm.where(ScoreModel::class.java).findAll()
        if (realmListForLevels.size > 0) {
            realmListForLevels.forEachIndexed { index, it ->
                levelsDataList.add(it)
                realm.beginTransaction()
                levelsDataList[index].thumbnail = thumbnails[index]
                realm.commitTransaction()
                if (!it.isLocked) {
                    currentLevel++
                }
            }
            level_score.text = levelsDataList.sumBy { it.levelScore }.toString()
        } else {
            for (i in 0..10) {
                val data = ScoreModel().apply {
                    levelNumber = i + 1
                    levelScore = 0
                    thumbnail = thumbnails[i]
                    isLocked = i != 0
                }
                levelsDataList.add(data)
            }
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(levelsDataList)
            realm.commitTransaction()
        }
    }


}