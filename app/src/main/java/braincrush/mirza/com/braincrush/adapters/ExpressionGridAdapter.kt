package braincrush.mirza.com.braincrush.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import braincrush.mirza.com.braincrush.interfaces.ExpressionTermSelectionListener

class ExpressionGridAdapter(private val mContext: Context, private val inputArrayList: ArrayList<String>, private val expressionTermSelectionListener: ExpressionTermSelectionListener) : BaseAdapter() {

    private val TAG = GridViewAdapter::class.java.simpleName
    override fun getCount(): Int = inputArrayList.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item: Button
        if (convertView == null) {
            item = Button(mContext)
            item.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            item.setTextColor(Color.rgb(80, 80, 80))
            item.text = inputArrayList[position]
        } else {
            item = convertView as Button
        }

        item.setOnClickListener {
            item.text = "-"
            item.isEnabled = false
            expressionTermSelectionListener.onTermSelected(item.text.toString()[0])
        }
        return item
    }
}