package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.adapters.NumberGridAdapter
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L2_NUM_COLUMN
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L2_NUM_COUNT
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L2_ROUND_COUNT
import braincrush.mirza.com.braincrush.extensions.animateShow
import braincrush.mirza.com.braincrush.extensions.getRoundScore
import braincrush.mirza.com.braincrush.extensions.random
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.AnswerSelectionListener
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_two.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.level_item.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*


class LevelTwoActivity : AppCompatActivity(), PausedMenuListener, AnswerSelectionListener {

    private val TAG = LevelTwoActivity::class.java.simpleName

    private val LEVEL_NUMBER = 2
    private val scoreArray = ArrayList<Int>()
    private var currentRound = 1
    private var gridViewHeight = 0
    private var adapter: NumberGridAdapter? = null
    private var randomNumbersList = ArrayList<Int>()
    private var forceCancel = false
    private var currentProgressValue = 0L
    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelTwoActivity)
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
        setContentView(R.layout.activity_level_two)

        setListeners()
        setDefaults()
    }

    override fun onBackPressed() {
        if (game_view.visibility == View.VISIBLE) {
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
            startActivity(Intent(this, LevelThreeActivity::class.java))
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
        round_heading.text = "Level $LEVEL_NUMBER"
        round_count_text.text = "Round 1 of $L2_ROUND_COUNT"
        level_desc.text = "Tap numbers in ascending order from 1 to $L2_NUM_COUNT"
        number_grid.numColumns = L2_NUM_COLUMN
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
        setRandomList()
        when (roundNumber) {
            1 -> {
                progressAnimator.duration = 40000
                adapter = NumberGridAdapter(this, randomNumbersList, gridViewHeight, L2_NUM_COLUMN, this)
                adapter?.setStartNumber(randomNumbersList.min()!!)

            }
            2 -> {
                progressAnimator.duration = 40000
                adapter = NumberGridAdapter(this, randomNumbersList, gridViewHeight, L2_NUM_COLUMN, this)
                adapter?.setRevers()
                adapter?.setStartNumber(randomNumbersList.max()!!)

            }
        }
        showGameView()
        progressAnimator.start()
    }

    private fun setRandomList() {
        randomNumbersList = ArrayList()
        for (i in 0 until L2_NUM_COUNT) {
            var number = (1..L2_NUM_COUNT).random()
            if (randomNumbersList.contains(number)) {
                while (randomNumbersList.contains(number)) {
                    number = (1..L2_NUM_COUNT).random()
                }
            }
            randomNumbersList.add(number)
        }
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
        val score = scoreArray.sumBy { it } / L2_ROUND_COUNT
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
        loose_round_count.text = "Round $currentRound of $L2_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onTimeUp() {
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L2_ROUND_COUNT"
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
        game_view.visibility = View.GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L2_ROUND_COUNT"
        level_desc.text = "Now tap numbers in descending order from $L2_NUM_COUNT to 1"
        instructions_view.visibility = View.VISIBLE
    }


    override fun onAnswerSelected(isCorrectAns: Boolean) {
        forceCancel = true
        progressAnimator.cancel()
        if (isCorrectAns) {
            scoreArray.add(getRoundScore(progress_bar.progress))
            if (currentRound == L2_ROUND_COUNT) {
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
