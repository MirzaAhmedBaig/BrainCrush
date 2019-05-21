package braincrush.mirza.com.braincrush.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.customs.RatingBar
import braincrush.mirza.com.braincrush.interfaces.LevelSelectionListener
import braincrush.mirza.com.braincrush.models.ScoreModel

class LevelsCarouselAdapter(private val levelsDataList: ArrayList<ScoreModel>) : RecyclerView.Adapter<LevelsCarouselAdapter.ViewHolder>() {

    private val TAG = LevelsCarouselAdapter::class.java.simpleName
    private var levelSelectionListener: LevelSelectionListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val baseContext: ImageView = itemView.findViewById(R.id.baseContext)
        val lockedLevels: View = itemView.findViewById(R.id.lockedLevels)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelsCarouselAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.level_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.baseContext.setImageResource(levelsDataList[position].thumbnail)
        if (!levelsDataList[position].isLocked) {
            holder.lockedLevels.visibility = View.GONE
        }
        holder.ratingBar.setRatingWithoutAnimation(levelsDataList[position].levelScore)
        holder.baseContext.setOnClickListener {
            levelSelectionListener!!.onLevelSelected(position, levelsDataList[position].isLocked)
        }
    }

    override fun getItemCount() = levelsDataList.size

    fun setLevelSelectionListener(levelSelectionListener: LevelSelectionListener) {
        this.levelSelectionListener = levelSelectionListener
    }
}