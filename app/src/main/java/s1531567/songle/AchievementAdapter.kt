package s1531567.songle

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.achievement_layout.view.*

/**
 * Created by holly on 07/11/17.
 */
class AchievementAdapter(val context: Context, private val achievements: List<Achievement>, private val itemClick : (Achievement)->Unit) : RecyclerView.Adapter<AchievementAdapter.AchievementHolder>() {


    override fun onBindViewHolder(holder: AchievementAdapter.AchievementHolder, position: Int) {
        holder.bind(achievements[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AchievementAdapter.AchievementHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        return AchievementHolder(context, layoutInflator.inflate(R.layout.achievement_layout, parent, false), itemClick)
    }

    override fun getItemCount(): Int {
        return achievements.size
    }


    class AchievementHolder(context: Context,view: View, itemClick: (Achievement) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mClick = itemClick


        fun bind(achievement: Achievement)  {
            with(achievement) {
                if (!prefs.checkAchievement(achievement.id)){
                    itemView.achievement_title.text = achievement.title.map { it -> '?' }.toString()
                    itemView.achievement_description.text = achievement.description.map { it -> '?' }.toString()


                }
                else {
                    itemView.achievement_title.text = achievement.title
                    itemView.achievement_description.text = achievement.description

                }
                itemView.setOnClickListener{mClick(this)}


            }

        }
    }

}