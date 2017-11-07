package s1531567.songle

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.songs_layout.view.*

/**
 * Created by holly on 07/11/17.
 */
class SongAdapter(val songs: List<Song>, val itemClick : (Song)->Unit ) : RecyclerView.Adapter<SongAdapter.SongHolder>() {


    override fun onBindViewHolder(holder: SongAdapter.SongHolder, position: Int) {
        holder.bind(songs[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongAdapter.SongHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        return SongHolder(layoutInflator.inflate(R.layout.songs_layout, parent, false), itemClick)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    class SongHolder(view: View, itemClick: (Song) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mClick = itemClick

        fun bind(song:Song)  {
            with(song) {
                itemView.songTitle.text = song.title
                itemView.songArtist.text = song.artist
                itemView.setOnClickListener{mClick(this)}
            }

        }
    }

}