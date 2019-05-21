package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.adapters.NumberGridAdapter
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L4_NUM_COLUMN
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L4_ROUND_COUNT
import braincrush.mirza.com.braincrush.extensions.animateShow
import braincrush.mirza.com.braincrush.extensions.doRandom
import braincrush.mirza.com.braincrush.extensions.getRoundScore
import braincrush.mirza.com.braincrush.extensions.random
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.AnswerSelectionListener
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_four.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.level_item.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*

class LevelFourActivity : AppCompatActivity(), PausedMenuListener, AnswerSelectionListener {

    private val TAG = LevelFourActivity::class.java.simpleName

    private val LEVEL_NUMBER = 4
    private val scoreArray = ArrayList<Int>()
    private var currentRound = 1
    private var gridViewHeight = 0
    private var adapter: NumberGridAdapter? = null
    private var randomNumbersList = ArrayList<Int>()
    private var forceCancel = false
    private var currentProgressValue = 0L
    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelFourActivity)
        }
    }

    private val progressAnimator by lazy {
        ObjectAnimator.ofInt(progress_bar, "progress", 0, progress_bar.max)
    }

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_level_four)

        fillInputArray()
        setListeners()
        setDefaults()
    }

    override fun onBackPressed() {
        if (game_view.visibility == VISIBLE) {
            menuDialog.show(supportFragmentManager, "menu_alert")
            pauseGame()
        } else {
            super.onBackPressed()
        }
    }


    private fun setListeners() {
        instructions_view.setOnClickListener {
            startRound(currentRound)
        }
        error_view.setOnClickListener {
            error_view.visibility = View.GONE
            game_view.visibility = View.GONE
            loose_game_view.visibility = View.VISIBLE
        }

        loose_retry.setOnClickListener {
            restartActivity()
        }
        loose_quit.setOnClickListener {
            finish()
        }

        retry_level_button.setOnClickListener {
            restartActivity()
        }
        next_level_button.setOnClickListener {
            //            startActivity(Intent(this, LevelFiveActivity::class.java))
            finish()
        }

        progressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Log.d(TAG, "Progress")
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!forceCancel) {
                    onTimeUp()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    private fun setDefaults() {
        setRandomList()
        round_heading.text = "Level $LEVEL_NUMBER"
        round_count_text.text = "Round 1 of $L4_ROUND_COUNT"
        level_desc.text = "Tap numbers in ascending order counting by multiple of number, starting from ${randomNumbersList.min()}"
        number_grid.numColumns = L4_NUM_COLUMN
        number_grid.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (number_grid.measuredHeight > 1) {
                    gridViewHeight = number_grid.measuredHeight - (number_grid.verticalSpacing * 5)
                    number_grid.visibility = View.VISIBLE
                    number_grid.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }


    private fun startRound(roundNumber: Int) {
        when (roundNumber) {
            1 -> {
                progressAnimator.duration = 40000
                adapter = NumberGridAdapter(this, randomNumbersList, gridViewHeight, L4_NUM_COLUMN, this)
                adapter?.setStartNumber(randomNumbersList.min()!!)

            }
            2 -> {
                progressAnimator.duration = 40000
                adapter = NumberGridAdapter(this, randomNumbersList, gridViewHeight, L4_NUM_COLUMN, this)
                adapter?.setRevers()
                adapter?.setStartNumber(randomNumbersList.max()!!)

            }
            3 -> {
                gridViewHeight = number_grid.measuredHeight - (number_grid.verticalSpacing * 3)
                progressAnimator.duration = 40000
                adapter = NumberGridAdapter(this, randomNumbersList, gridViewHeight, 3, this)
                adapter?.setStartNumber(randomNumbersList.min()!!)
                adapter?.setLikeFibonacci()
            }
        }
        adapter?.setIncrementingCount(randomNumbersList.min()!!)
        showGameView()
        progressAnimator.start()
    }


    private fun fillInputArray() {
        val randomStart = (2..6).random()
        (randomStart..randomStart * 24 step randomStart).forEach {
            randomNumbersList.add(it)
        }
    }

    private fun setRandomList() {
        if (currentRound == 3) {
            randomNumbersList = ArrayList()
            val randomStart = (1..4).random()
            randomNumbersList.add(randomStart)
            (0 until 11).forEach {
                randomNumbersList.add(randomNumbersList.sum())
            }
            number_grid.numColumns = 3
        }
        randomNumbersList = randomNumbersList.doRandom()

    }

    private fun showGameView() {
        forceCancel = false
        number_grid.adapter = null
        number_grid.adapter = adapter
        progress_bar.progress = 0
        instructions_view.visibility = View.GONE
        number_grid.visibility = View.VISIBLE
        game_view.visibility = View.VISIBLE
    }

    private fun showResult() {
        val score = scoreArray.sumBy { it } / L4_ROUND_COUNT
        if (score == 5) {
            result_msg.text = "You have good eye!"
        }
        result_heading.text = "Level $LEVEL_NUMBER"
        game_view.visibility = View.GONE
        result_view.animateShow({
            congo_rating_bar.setRating(score)
        })

        val currentLevel = realm.where(ScoreModel::class.java).equalTo("levelNumber", LEVEL_NUMBER).findFirst()
        if (currentLevel!!.levelScore < score) {
            val nextLevel = realm.where(ScoreModel::class.java).equalTo("levelNumber", LEVEL_NUMBER + 1).findFirst()
            realm.beginTransaction()
            currentLevel.levelScore = score
            nextLevel?.isLocked = false
            realm.copyToRealmOrUpdate(nextLevel!!)
            realm.copyToRealmOrUpdate(currentLevel)
            realm.commitTransaction()
        }
    }

    private fun lostCurrentRound() {
        loose_round_count.text = "Round $currentRound of $L4_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onTimeUp() {
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L4_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }


    private fun pauseGame() {
        if (progressAnimator.isRunning) {
            forceCancel = true
            currentProgressValue = progressAnimator.currentPlayTime
            progressAnimator.cancel()
        }

    }

    private fun resumeGame() {
        progressAnimator.start()
        progressAnimator.currentPlayTime = currentProgressValue
    }

    private fun restartActivity() {
        rating_bar.setRating(0, false)
        recreate()
    }


    private fun showNextRound() {
        setRandomList()
        game_view.visibility = View.GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L4_ROUND_COUNT"

        level_desc.text = if (currentRound == 2)
            "Now tap numbers in descending order counting by multiple of number, starting from ${randomNumbersList.max()}"
        else
            "Now tap numbers such that, next number you tap will be addition of all previously tapped numbers, starting from ${randomNumbersList.min()}"
        instructions_view.visibility = View.VISIBLE
    }


    override fun onAnswerSelected(isCorrectAns: Boolean) {
        forceCancel = true
        progressAnimator.cancel()
        if (isCorrectAns) {
            scoreArray.add(getRoundScore(progress_bar.progress))
            if (currentRound == L4_ROUND_COUNT) {
                showResult()
            } else {
                currentRound++
                showNextRound()
            }
        } else {
            lostCurrentRound()
        }
    }

    override fun stopTimer() {
        forceCancel = true
        progressAnimator.cancel()
    }


    override fun onResumeGame() {
        resumeGame()
    }

    override fun onRestartGame() {
        restartActivity()
    }

    override fun onQuitGame() {
        finish()
    }


}
