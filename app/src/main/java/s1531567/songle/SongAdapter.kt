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
class SongAdapter(val songs: List<Song>) : RecyclerView.Adapter<SongAdapter.SongHolder>() {


    override fun onBindViewHolder(holder: SongHolder?, position: Int) {
        holder!!.bind(songs[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongAdapter.SongHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        //val viewholder = ViewHolder(layoutInflator.inflate(R.layout.songs_layout, parent, false))
        return SongHolder(layoutInflator.inflate(R.layout.songs_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return songs.size
    }



//    class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        fun bind(song: Song, listener: (Song) -> Unit) = with(itemView) {
//            itemView.songTitle.text = song.title
//            itemView.songArtist.text = song.artist
//            setOnClickListener { listener(song) }
//        }
//
//    }

    class SongHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v


        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("Song key: ${SONG_KEY}")
            println(("Song: ${song}"))
            val showSongIntent = Intent(itemView.context, ReviewActivity::class.java)
            showSongIntent.putExtra(SONG_KEY, song!!.title)
            itemView.context.startActivity(showSongIntent)

        }

        fun bind(song:Song)  {
            itemView.songTitle.text = song.title
            itemView.songArtist.text = song.artist

        }
    }

}