package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.adapters.GridViewAdapter
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_NUM_COLUMN
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_NUM_COUNT
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_R1_Q_PAIR
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_R2_Q_PAIR
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_R3_Q_PAIR
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_R4_Q_PAIR
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L1_ROUND_COUNT
import braincrush.mirza.com.braincrush.extensions.animateShow
import braincrush.mirza.com.braincrush.extensions.getRoundScore
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.AnswerSelectionListener
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_one.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.level_item.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*

class LevelOneActivity : AppCompatActivity(), PausedMenuListener, AnswerSelectionListener {

    private val TAG = LevelOneActivity::class.java.simpleName

    private val LEVEL_NUMBER = 1
    private val scoreArray = ArrayList<Int>()
    private var currentRound = 1
    private var gridViewHeight = 0
    private var adapter: GridViewAdapter? = null
    private var ansPosition = 0
    private var forceCancel = false
    private var currentProgressValue = 0L
    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelOneActivity)
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
        setContentView(R.layout.activity_level_one)

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
            error_view.visibility = GONE
            game_view.visibility = GONE
            loose_game_view.visibility = VISIBLE
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
            startActivity(Intent(this, LevelTwoActivity::class.java))
            finish()
        }

        progressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
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
        level_desc.text = "Find and tap the number 1"
        number_grid.numColumns = L1_NUM_COLUMN
        number_grid.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (number_grid.measuredHeight > 1) {
                    gridViewHeight = number_grid.measuredHeight - (number_grid.verticalSpacing * 11)
                    number_grid.visibility = VISIBLE
                    number_grid.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

        })
    }


    private fun startRound(roundNumber: Int) {
        setRandomPosition()
        when (roundNumber) {
            1 -> {
                progressAnimator.duration = 10000
                adapter = GridViewAdapter(this, L1_R1_Q_PAIR, L1_NUM_COUNT, ansPosition, gridViewHeight, L1_NUM_COLUMN, this)
            }
            2 -> {
                progressAnimator.duration = 8000
                adapter = GridViewAdapter(this, L1_R2_Q_PAIR, L1_NUM_COUNT, ansPosition, gridViewHeight, L1_NUM_COLUMN, this)
            }
            3 -> {
                progressAnimator.duration = 6000
                adapter = GridViewAdapter(this, L1_R3_Q_PAIR, L1_NUM_COUNT, ansPosition, gridViewHeight, L1_NUM_COLUMN, this)
            }
            4 -> {
                progressAnimator.duration = 4000
                adapter = GridViewAdapter(this, L1_R4_Q_PAIR, L1_NUM_COUNT, ansPosition, gridViewHeight, L1_NUM_COLUMN, this)
            }
        }
        showGameView()
        progressAnimator.start()
    }

    private fun setRandomPosition() {
        val random = (Math.random() * (L1_NUM_COUNT - 1)).toInt()
        if (random == ansPosition) {
            while (ansPosition == random) {
                ansPosition = (Math.random() * (L1_NUM_COUNT - 1)).toInt()
            }
        } else {
            ansPosition = random
        }

    }

    private fun showGameView() {
        forceCancel = false
        number_grid.adapter = null
        number_grid.adapter = adapter
        progress_bar.progress = 0
        instructions_view.visibility = GONE
        number_grid.visibility = VISIBLE
        game_view.visibility = VISIBLE
    }

    private fun showResult() {
        val score = scoreArray.sumBy { it } / L1_ROUND_COUNT
        if (score == 5) {
            result_msg.text = "You have good eye!"
        }
        result_heading.text = "Level $LEVEL_NUMBER"
        game_view.visibility = GONE
        result_view.animateShow({
            congo_rating_bar.setRating(score)
            congo_rating_bar.visibility = VISIBLE
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
        if (currentRound == 3) {
            loose_subheading.text = "You tapped the wrong Character"
        }
        loose_round_count.text = "Round $currentRound of $L1_ROUND_COUNT"
        error_view.visibility = VISIBLE
    }

    private fun onTimeUp() {
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L1_ROUND_COUNT"
        error_view.visibility = VISIBLE
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

    private fun showNextRound() {
        game_view.visibility = GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L1_ROUND_COUNT"
        when (currentRound) {
            2 -> {
                level_desc.text = "Now find and tap Number 3"
            }
            3 -> {
                level_desc.text = "Now find and tap Character O"
            }
            4 -> {
                level_desc.text = "Now find and tap Number 8"
            }
        }
        instructions_view.animateShow()
    }

    private fun restartActivity() {
        rating_bar.setRating(0, false)
        recreate()
    }


    override fun onAnswerSelected(isCorrectAns: Boolean) {
        if (isCorrectAns) {
            scoreArray.add(getRoundScore(progress_bar.progress))
            if (currentRound == L1_ROUND_COUNT) {
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

