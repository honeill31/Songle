package s1531567.songle

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.map_layout.view.*

/**
 * Created by holly on 08/11/17.
 */

/* This class controls the display of Map progress for each Song in the Review Activity */
class MapAdapter (val maps: List<MapInfo>, private val itemClick : (MapInfo)->Unit ) : RecyclerView.Adapter<MapAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapAdapter.ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        return MapAdapter.ViewHolder(layoutInflator.inflate(R.layout.map_layout, parent, false), itemClick)
    }

    override fun onBindViewHolder(holder: MapAdapter.ViewHolder, position: Int) {
        holder.bind(maps[position])
    }

    override fun getItemCount(): Int {
        return maps.size
    }


    class ViewHolder(view: View, itemClick: (MapInfo) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mClick = itemClick


        fun bind(map: MapInfo) {
            with(map) {
                val mapnum = map.mapNumber
                val collected = map.collectedPlacemark
                val total = map.totalPlacemark
                itemView.map_number.text = "Map $mapnum"

                if (map.locked){
                    itemView.locked.setImageResource(R.drawable.ic_lock_outline_black_24dp)
                    itemView.collected_placemarks_review.text = ""

                }
                if (!map.locked){
                    itemView.collected_placemarks_review.text = "$collected/$total"
                    itemView.locked.visibility = View.INVISIBLE
                }

                itemView.setOnClickListener {mClick(this)}
                itemView.setOnLongClickListener{
                    mClick(this)
                    true
                }
            }

        }


    }
}