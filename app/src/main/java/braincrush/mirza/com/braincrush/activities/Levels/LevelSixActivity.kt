package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L6_ROUND_COUNT
import braincrush.mirza.com.braincrush.customs.WheelView
import braincrush.mirza.com.braincrush.extensions.*
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.models.ScoreModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_six.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*
import kotlin.math.max
import kotlin.math.min

class LevelSixActivity : AppCompatActivity(), PausedMenuListener {

    private val TAG: String = LevelSixActivity::class.java.simpleName
    private val LEVEL_NUMBER = 6
    private var currentRound = 1
    private var inputArrayList = ArrayList<String>()
    private var forceCancel = false
    private var currentProgressValue = 0L
    private var sec = 29
    private var msec = 9
    private var correctAnswers = 0
    private var atempted = 0
    private var score = 0
    private var timeTaken = 0

    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelSixActivity)
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
        setContentView(R.layout.activity_level_six)

        fillInputArray()
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
            startRound()
        }
        error_view.setOnClickListener {
            error_view.visibility = View.GONE
            game_view.visibility = View.GONE
            loose_game_view.visibility = View.VISIBLE
        }

        loose_retry.setOnClickListener {
            restartGame()
        }
        loose_quit.setOnClickListener {
            finish()
        }

        retry_level_button.setOnClickListener {
            restartGame()
        }
        next_level_button.setOnClickListener {
            startActivity(Intent(this, LevelSevenActivity::class.java))
            finish()
        }


        progressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Log.d(TAG, "Progress")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "After Aniim Val : ${remaining_time.text}")
                if (!forceCancel) {
                    onTimeUp()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                handler.postDelayed(timerRunnable, 100)
            }

        })

        correct_button.setOnClickListener {
            checkAndSetNextExpression(true)
        }
        incorrect_button.setOnClickListener {
            checkAndSetNextExpression(false)
        }
    }

    private fun fillInputArray() {
        inputArrayList = ArrayList()
        var numOne: Int
        var numTwo: Int
        (0..9).forEach {
            val operator = getRandomOperatorAll()
            if (operator == '/') {
                val pair = getRandomDividerPair()
                numOne = max(pair.first, pair.second)
                numTwo = min(pair.first, pair.second)
                inputArrayList.add("$numOne $operator $numTwo = ${getExpressionResult("$numOne$operator$numTwo").toInt()}")
            } else {
                if (operator == '*') {
                    numOne = getSmallRandomNumber()
                    numTwo = getSmallRandomNumber()
                } else {
                    numOne = getRandomNumber()
                    numTwo = getRandomNumber()
                }

                if (currentRound == L6_ROUND_COUNT) {
                    val numThree = getRandomNumber()
                    val operatorTwo = getRandomOperator()
                    inputArrayList.add("$numOne $operator $numTwo $operatorTwo $numThree = ${getExpressionResult("$numOne$operator$numTwo$operatorTwo$numThree").toInt()}")
                } else {
                    inputArrayList.add("$numOne $operator $numTwo = ${getExpressionResult("$numOne$operator$numTwo").toInt()}")
                }
            }
        }

        inputArrayList.forEach {
            Log.d(TAG, "Input Array Items : $it")
        }

        Log.d(TAG, "\nArray Size : ${inputArrayList.size}")

        (0..(0..6).random()).forEach {
            val indexOne = (0..inputArrayList.lastIndex).random()
            Log.d(TAG, "Random Index : $indexOne")
            Log.d(TAG, "Array IISIZE : ${inputArrayList.size}")
            Log.d(TAG, "Random Result : ${inputArrayList[indexOne].split("=")[1]}")
            val temp: String = inputArrayList[indexOne].split("=")[1]
            inputArrayList[indexOne] = inputArrayList[indexOne].split("=")[0] + "=" + inputArrayList[it].split("=")[1]
            inputArrayList[it] = inputArrayList[it].split("=")[0] + "=" + temp
        }
        inputArrayList.forEach {
            Log.d(TAG, "OutPut Array Items : $it")
        }
        setExpressionWheel()

    }

    private fun setDefaults() {
        round_heading.text = "Level $LEVEL_NUMBER"
        round_count_text.text = "Round 1 of $L6_ROUND_COUNT"
        level_desc.text = "You have given some random expression with some result you have to find is the showing expression is correct ot not."
    }


    private fun startRound() {
        progressAnimator.duration = 30000
        progressAnimator.startDelay = 0
        showGameView()
        progressAnimator.start()
    }

    private fun showGameView() {
        forceCancel = false
        progress_bar.progress = 0
        correctAnswers = 0
        atempted = 0
        sec = 29
        msec = 9
        remaining_time.text = "Time : 29:9"
        correct_ans.text = "Correct : 0 / 0"
        instructions_view.visibility = View.GONE
        game_view.visibility = View.VISIBLE
    }

    private fun showNextRound() {
        game_view.visibility = View.GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L6_ROUND_COUNT"
        level_desc.text = "Yo! Now try the same again"
        instructions_view.visibility = View.VISIBLE
    }

    private fun lostCurrentRound() {
        loose_subheading.text = "You should at least give two correct answers"
        loose_round_count.text = "Round $currentRound of $L6_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onTimeUp() {
        handler.removeCallbacks(timerRunnable)
        remaining_time.text = "Time : 00:0"
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L6_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun showResult() {
        val expectedTime = correctAnswers * 2500
        score /= 2
        if (score == 5) {
            result_msg.text = "You have good mathematics abilities!"
        }
        if (timeTaken / 2 <= expectedTime) {
            //unlock more levels
            result_msg.text = "You are fast!"
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

    private fun restartGame() {
        recreate()
    }

    override fun onResumeGame() {
        resumeGame()
    }

    override fun onRestartGame() {
        restartGame()
    }

    override fun onQuitGame() {
        finish()
    }


    private fun setExpressionWheel() {
        expressions_wheel.offset = 1
        expressions_wheel.setItems(inputArrayList)
        expressions_wheel.onWheelViewListener = object : WheelView.OnWheelViewListener() {
            override fun onSelected(selectedIndex: Int, item: String) {
                Log.d(TAG, "selectedIndex: $selectedIndex, item: $item")
            }
        }
        expressions_wheel.setSeletion(0)
    }


    private fun checkAndSetNextExpression(selectedAnswer: Boolean) {
        atempted++
        val data = expressions_wheel.seletedItem!!.split("=")
        if (selectedAnswer == (getExpressionResult(data[0].trim()).toInt().toString() == data[1].trim())) {
            correctAnswers++
        }
        if (expressions_wheel.selectedIndex == inputArrayList.lastIndex) {
            forceCancel = true
            progressAnimator.cancel()
            if (correctAnswers > 1) {
                score += (correctAnswers / 2) + (correctAnswers % 2)
                timeTaken += progress_bar.progress
                if (currentRound == L6_ROUND_COUNT) {
                    showResult()
                } else {
                    currentRound++
                    clearWheel()
                    fillInputArray()
                    showNextRound()
                }
            } else {
                lostCurrentRound()
            }
        } else {
            expressions_wheel.setSeletion(expressions_wheel.selectedIndex + 1)
        }
        correct_ans.text = "Correct : $correctAnswers / $atempted"
    }

    private fun clearWheel() {
        expressions_wheel.clearWheel()
        val PLANETS = arrayOf("Mars", "moon")
        val DUM: Array<String> = arrayOf(*PLANETS)
    }

    private val handler = Handler()

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (msec == 0) {
                msec = 9
                sec--
            } else {
                msec--
            }
            remaining_time.text = "Time : $sec:$msec"
            if (sec == 0 && msec == 0) {
                handler.removeCallbacks(this)
            } else {
                handler.postDelayed(this, 100)
            }

        }

    }
}
