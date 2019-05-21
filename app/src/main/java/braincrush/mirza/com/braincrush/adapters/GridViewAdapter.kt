package braincrush.mirza.com.braincrush.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.extensions.animateHide
import braincrush.mirza.com.braincrush.extensions.animateText
import braincrush.mirza.com.braincrush.interfaces.AnswerSelectionListener

class GridViewAdapter(private val mContext: Context, private val inputPair: Pair<String, String>, private val totalCount: Int, private val ansPosition: Int, private val gridViewHeight: Int, private val columnNum: Int, private val answerSelectionListener: AnswerSelectionListener) : BaseAdapter() {

    private val TAG = GridViewAdapter::class.java.simpleName
    override fun getCount(): Int = totalCount

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d(TAG, "Pair (${inputPair.first}, ${inputPair.second}) Count : $totalCount Random Position : $ansPosition Grid Height : $gridViewHeight Column Num : $columnNum")
        val item: Button
        if (convertView == null) {
            item = Button(mContext)
            item.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gridViewHeight / (totalCount / columnNum))
            item.setBackgroundResource(R.drawable.button)
            item.setTextColor(Color.rgb(80, 80, 80))
            if (position == ansPosition) {
                item.text = inputPair.second
                //Debugging Purpose
                item.setTextColor(Color.BLUE)
            } else
                item.text = inputPair.first

        } else {
            item = convertView as Button
        }

        item.setOnClickListener {
            val isCorrectAns = position == ansPosition
            if (isCorrectAns) {
                answerSelectionListener.stopTimer()
                (0 until totalCount).forEach {
                    if (it != position)
                        (parent.getChildAt(it) as Button).animateHide {
                            if (it == totalCount - 1) {
                                answerSelectionListener.onAnswerSelected(isCorrectAns)
                            }
                        }
                }

            } else {
                item.setTextColor(Color.RED)
                (parent.getChildAt(ansPosition) as Button).animateText()
                answerSelectionListener.onAnswerSelected(isCorrectAns)
            }

        }

        return item
    }
}