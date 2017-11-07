package s1531567.songle

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.songs_layout.view.*

/**
 * Created by holly on 07/11/17.
 */
class SongAdapter(val songs: List<Song>, context: Context, layout: Int) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflator.inflate(R.layout.songs_layout, parent, false))
    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])


    }
    override fun getItemCount(): Int {
        return songs.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song:Song) {
            itemView.songTitle.text = song.title
            itemView.songArtist.text = song.artist

        }
    }
}