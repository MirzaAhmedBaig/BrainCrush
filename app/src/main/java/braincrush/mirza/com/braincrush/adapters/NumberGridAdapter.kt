package braincrush.mirza.com.braincrush.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.extensions.animateText
import braincrush.mirza.com.braincrush.interfaces.AnswerSelectionListener

class NumberGridAdapter(private val mContext: Context, private val numberList: ArrayList<Int>, private val gridViewHeight: Int, private val columnNum: Int, private val answerSelectionListener: AnswerSelectionListener) : BaseAdapter() {

    private val TAG = NumberGridAdapter::class.java.simpleName
    override fun getCount(): Int = numberList.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    fun setRevers() {
        isRevers = true
        isLikeFibonacci = false
    }

    fun setStartNumber(startNumber: Int) {
        nextNumber = startNumber

    }

    fun setIncrementingCount(count: Int) {
        countToIncrease = count
    }

    fun setLikeFibonacci() {
        isLikeFibonacci = true
        isRevers = false
    }

    private var nextNumber = 1
    private var countToIncrease = 1
    private var isRevers = false
    private var isLikeFibonacci = false
    private var sum = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item: Button
        if (convertView == null) {
            item = Button(mContext)
            item.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gridViewHeight / (numberList.size / columnNum))
            item.setBackgroundResource(R.drawable.button)
            item.setTextColor(Color.rgb(10, 10, 10))
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)

            item.text = numberList[position].toString()

        } else {
            item = convertView as Button
        }

        item.setOnClickListener {
            sum += item.text.toString().toInt()
            if (item.text.toString().toInt() == nextNumber) {
                item.isEnabled = false
                item.text = ""
                if (isRevers) {
                    if (nextNumber == numberList.min()!!) {
                        answerSelectionListener.onAnswerSelected(true)
                        return@setOnClickListener
                    } else {
                        nextNumber -= countToIncrease
                    }
                } else if (isLikeFibonacci) {
                    if (nextNumber == numberList.max()!!) {
                        answerSelectionListener.onAnswerSelected(true)
                        return@setOnClickListener
                    } else {
                        nextNumber = sum
                    }
                } else {
                    if (nextNumber == numberList.max()!!) {
                        answerSelectionListener.onAnswerSelected(true)
                        return@setOnClickListener
                    } else {
                        nextNumber += countToIncrease
                    }
                }
                //debugging purpose
                if (numberList.contains(nextNumber)) {
                    Log.d(TAG, "Index of Child : ${numberList.indexOf(nextNumber)}")
                    (parent.getChildAt(numberList.indexOf(nextNumber)) as Button).setTextColor(Color.BLUE)
                }

            } else {
                item.setTextColor(Color.RED)
                val btn = parent.getChildAt(numberList.indexOfFirst { it == nextNumber }) as Button
                val btn2 = parent.getChildAt(numberList.indexOfLast { it == nextNumber }) as Button?
                btn.animateText()
                btn2?.animateText()
                answerSelectionListener.onAnswerSelected(false)
            }


        }

        return item
    }
}