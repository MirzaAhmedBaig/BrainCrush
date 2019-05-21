package braincrush.mirza.com.braincrush.activities.Levels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.constants.GameConstants.Companion.L7_ROUND_COUNT
import braincrush.mirza.com.braincrush.extensions.animateShow
import braincrush.mirza.com.braincrush.extensions.getExpressionResult
import braincrush.mirza.com.braincrush.extensions.getRoundScore
import braincrush.mirza.com.braincrush.extensions.isOperator
import braincrush.mirza.com.braincrush.fragments.AlertDialogFragment
import braincrush.mirza.com.braincrush.fragments.MenuDialogFragment
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import braincrush.mirza.com.braincrush.utils.RandomExpressionGenerator
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_level_seven.*
import kotlinx.android.synthetic.main.before_level_view.*
import kotlinx.android.synthetic.main.loose_game_view.*
import kotlinx.android.synthetic.main.result_view.*

class LevelSevenActivity : AppCompatActivity(), PausedMenuListener {

    private val TAG = LevelSevenActivity::class.java.simpleName

    private val LEVEL_NUMBER = 7
    private var currentRound = 1
    private var inputArrayList = ArrayList<String>()
    private val progressScoreList = ArrayList<Int>()
    private var forceCancel = false
    private var currentProgressValue = 0L
    private var isOperatorAllowed = false
    private var numberCount = 6
    private val menuDialog by lazy {
        MenuDialogFragment().apply {
            setMenuListener(this@LevelSevenActivity)
        }
    }

    private val messageDialog by lazy {
        AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", "Oops!")
                putString("msg", "You should use all numbers")
                putInt("type", AlertDialogFragment.SIMPLE_TYPE)
            }
            isCancelable = false
        }

    }

    private val randomExpressionGenerator by lazy {
        RandomExpressionGenerator(false)
    }

    private val progressAnimator by lazy {
        ObjectAnimator.ofInt(progress_bar, "progress", 0, progress_bar.max)
    }

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    private val buttons by lazy {
        arrayOf(button_one, button_two, button_three, button_four, button_five, button_six)
    }

    private val operators by lazy {
        arrayOf(plus, minus, multi, divide)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_level_seven)

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
            startActivity(Intent(this, LevelFourActivity::class.java))
            finish()
        }

        check_button.setOnClickListener {
            onAnswerSelected()
        }

        clear_expression.setOnClickListener {
            resetExpression()
        }

        operators.forEach {
            it.setOnClickListener { view ->
                addOperator((view as Button).text.toString())
            }
        }

        buttons.forEach {
            it.setOnClickListener { view ->
                expression.text = expression.text.toString() + (view as Button).text.toString()
                view.text = "."
                view.isEnabled = false
                isOperatorAllowed = true
                numberCount--
            }
        }

        undo_button.setOnClickListener {
            undoExpression()
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

    private fun fillInputArray() {
        numberCount = 6
        inputArrayList = ArrayList()
        randomExpressionGenerator.getInputNumbersArray().forEachIndexed { index, it ->
            inputArrayList.add(it)
            buttons[index].text = it
            buttons[index].isEnabled = true
            Log.d(TAG, "Number : $index : $it")
        }
        resulting_number.text = randomExpressionGenerator.getResult().toString()
        expression.text = ""
        Log.d(TAG, "Expression : ${randomExpressionGenerator.getExpression()}")

    }

    private fun setDefaults() {
        round_heading.text = "Level $LEVEL_NUMBER"
        round_count_text.text = "Round 1 of $L7_ROUND_COUNT"
        level_desc.text = "You have given six numbers and four arithmetic operators. You have to find the expression that is satisfying the given target number."
    }

    private fun addOperator(operator: String) {
        if (isOperatorAllowed && numberCount > 0) {
            expression.text = expression.text.toString() + operator
            isOperatorAllowed = false
        }
    }

    private fun undoExpression() {
        var text = expression.text
        if (text.isNotEmpty()) {
            val lastChar = text[text.lastIndex]
            expression.text = text.toString().substring(0, text.toString().length - 1)
            if (!isOperator(lastChar)) {
                val index = inputArrayList.indexOf(lastChar.toString())
                buttons[index].isEnabled = true
                buttons[index].text = lastChar.toString()
                numberCount++
            }
        }
        text = expression.text
        if (text.isEmpty() || isOperator(text[text.lastIndex])) {
            isOperatorAllowed = false
        }
    }

    private fun resetExpression() {
        expression.text = ""
        inputArrayList.forEachIndexed { index, number ->
            buttons[index].text = number
            buttons[index].isEnabled = true
        }
        numberCount = 6
        isOperatorAllowed = false
    }

    private fun startRound() {
        progressAnimator.duration = 50000
        showGameView()
        progressAnimator.start()
    }

    private fun showGameView() {
        forceCancel = false
        progress_bar.progress = 0
        instructions_view.visibility = View.GONE
        game_view.visibility = View.VISIBLE
    }

    private fun showNextRound() {
        game_view.visibility = View.GONE
        round_heading.text = "Congrats !"
        round_count_text.text = "Round $currentRound of $L7_ROUND_COUNT"
        level_desc.text = "Yo! Now try the same with big numbers"
        instructions_view.visibility = View.VISIBLE
    }

    private fun lostCurrentRound() {
        loose_subheading.text = "You entered a wrong expression"
        loose_round_count.text = "Round $currentRound of $L7_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onTimeUp() {
        loose_subheading.text = "Time is up!"
        loose_round_count.text = "Round $currentRound of $L7_ROUND_COUNT"
        error_view.visibility = View.VISIBLE
    }

    private fun onAnswerSelected() {
        if (expression.text.toString().isEmpty() || numberCount != 0) {
            messageDialog.show(supportFragmentManager, "alert_dialog")
            return
        }
        forceCancel = true
        progressAnimator.cancel()
        val isCorrectAns = getExpressionResult(expression.text.toString()).toInt().toString() == randomExpressionGenerator.getResult().toString()
        if (isCorrectAns) {
            progressScoreList.add(getRoundScore(progress_bar.progress))
            if (currentRound == L7_ROUND_COUNT) {
                showResult()
            } else {
                currentRound++
                randomExpressionGenerator.setIsAdavanced(true)
                fillInputArray()
                showNextRound()
            }
        } else {
            lostCurrentRound()
        }
    }

    private fun showResult() {
        val score = getRoundScore(progressScoreList.sum() / L7_ROUND_COUNT)
        if (score == 5) {
            result_msg.text = "You have good mathematics abilities!"
        }
        result_heading.text = "Level $LEVEL_NUMBER"
        game_view.visibility = View.GONE
        result_view.animateShow({
            congo_rating_bar.setRating(score)
        })


        /*val currentLevel = realm.where(ScoreModel::class.java).equalTo("levelNumber", LEVEL_NUMBER).findFirst()
        if (currentLevel!!.levelScore < score) {
            val nextLevel = realm.where(ScoreModel::class.java).equalTo("levelNumber", LEVEL_NUMBER + 1).findFirst()
            realm.beginTransaction()
            currentLevel.levelScore = score
            nextLevel?.isLocked = false
            realm.copyToRealmOrUpdate(nextLevel!!)
            realm.copyToRealmOrUpdate(currentLevel)
            realm.commitTransaction()
        }*/
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


}
