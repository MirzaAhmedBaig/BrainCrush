package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L5_ROUND_COUNT
import braincrush.mirza.com.braincrush.extensions.animateShow
import braincrush.mirza.com.braincrush.extensions.getRoundScore
import braincrush.mirza.com.braincrush.extensions.random
import braincrush.mirza.com.braincrush.fragments.AlertDialogFragment
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.OnDrawCompleteListener
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_five.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*

class LevelFiveActivity : AppCompatActivity(), PausedMenuListener, OnDrawCompleteListener {

    private val TAG = LevelFiveActivity::class.java.simpleName

    private val LEVEL_NUMBER = 5
    private var currentRound = 1
    private var randomNumbersList = ArrayList<Int>()
    private var forceCancel = false
    private var currentProgressValue = 0L
    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelFiveActivity)
        }
    }

    private val messageDialog by lazy {
        AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", "Oops!")
                putString("msg", "Fill all numbers")
                putInt("type", AlertDialogFragment.SIMPLE_TYPE)
            }
            isCancelable = false
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
        setContentView(R.layout.activity_level_five)

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
            startActivity(Intent(this, LevelSevenActivity::class.java))
            finish()
        }

        check_button.setOnClickListener {
            onAnswerSelected()
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
        round_count_text.text = "Round 1 of $L5_ROUND_COUNT"
        level_desc.text = "You have given a triangle having numbered edges and you have to find number of vertices such that addition of adjacent vertices should be equal to their edge"
        number_triangle.setListener(this)
    }


    private fun startRound(roundNumber: Int) {


        showGameView()
        progressAnimator.duration = 100000
        progressAnimator.start()
    }


    private fun fillInputArray() {
        (0..2).forEach {
            randomNumbersList.add((2..30).random())
        }
        val firstNum = randomNumbersList[0]
        (0..2).forEach {
            Log.d(TAG, "Number $it : ${randomNumbersList[it]}")
            if (it == 2)
                randomNumbersList[it] = randomNumbersList[it] + firstNum
            else
                randomNumbersList[it] = randomNumbersList[it] + randomNumbersList[(it + 1) % randomNumbersList.size]
        }
        number_triangle.setNumbers(randomNumbersList[0], randomNumbersList[2], randomNumbersList[1])


    }


    private fun showGameView() {
        forceCancel = false
        progress_bar.progress = 0
        instructions_view.visibility = View.GONE
        game_view.visibility = View.VISIBLE
    }

    private fun showResult() {
        val score = getRoundScore(progress_bar.progress)
        if (score == 5) {
            result_msg.text = "You have good mind!"
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
        loose_subheading.text = "You entered wrong numbers"
        loose_round_count.text = "Round $currentRound of $L5_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onTimeUp() {
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L5_ROUND_COUNT"
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


    private fun showNextRound() {
        game_view.visibility = View.GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L5_ROUND_COUNT"
        level_desc.text = "Now tap numbers in descending order counting by multiple of number, starting from ${randomNumbersList.max()}"
        instructions_view.visibility = View.VISIBLE
    }


    private fun onAnswerSelected() {
        if (number_one.text.toString().isEmpty() || number_two.text.toString().isEmpty() || number_three.text.toString().isEmpty()) {
            messageDialog.show(supportFragmentManager, "oops_alert")
            return
        }
        forceCancel = true
        progressAnimator.cancel()

        val sumOne = number_one.text.toString().toInt() + number_two.text.toString().toInt()
        val sumTwo = number_two.text.toString().toInt() + number_three.text.toString().toInt()
        val sumThree = number_three.text.toString().toInt() + number_one.text.toString().toInt()
        if (randomNumbersList[0] == sumOne && randomNumbersList[1] == sumTwo && randomNumbersList[2] == sumThree) {
            showResult()
        } else {
            lostCurrentRound()
        }
    }

    private fun restartActivity() {
        number_one.setText("0")
        number_two.setText("0")
        number_three.setText("0")
        recreate()
    }

    override fun onDrawCompletes(positionsList: ArrayList<Pair<Float, Float>>) {
        Log.d(TAG, "Position Size : ${positionsList.size}")
        var param = number_one.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, (positionsList[0].second - (number_one.measuredHeight / 2)).toInt(), 0, 0)
        number_one.layoutParams = param
        val param2 = number_two.layoutParams as FrameLayout.LayoutParams
        param2.setMargins((positionsList[1].first - number_two.measuredWidth / 2).toInt(), (positionsList[1].second - number_two.measuredHeight / 2).toInt(), 0, 0)
        number_two.layoutParams = param2

        val param3 = number_three.layoutParams as FrameLayout.LayoutParams
        param3.setMargins((positionsList[2].first - number_three.measuredWidth / 2).toInt(), (positionsList[2].second - number_three.measuredHeight / 2).toInt(), 0, 0)
        number_three.layoutParams = param3


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
