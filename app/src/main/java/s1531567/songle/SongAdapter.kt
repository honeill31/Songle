package s1531567.songle

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.songs_layout.view.*
import java.util.*

/**
 * Created by holly on 07/11/17.
 */
class SongAdapter(val context: Context,val songs: List<Song>, val itemClick : (Song)->Unit) : RecyclerView.Adapter<SongAdapter.SongHolder>() {


    override fun onBindViewHolder(holder: SongAdapter.SongHolder, position: Int) {
        holder.bind(songs[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SongAdapter.SongHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        return SongHolder(context, layoutInflator.inflate(R.layout.songs_layout, parent, false), itemClick)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    class SongHolder(context: Context,view: View, itemClick: (Song) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mClick = itemClick
        val pref = context.getSharedPreferences(context.getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()

        fun dummyPlacemarks() : String {
            val rand = Random()
            val collected = rand.nextInt(100-0) + 0
            val total = rand.nextInt(500)
            return "$collected/$total"

        }
        fun dummyGuessed(): Int {
            val guessed = 10000
            val notguessed = 22222

            return guessed or notguessed


        }

        fun dummySong(song:Song) : List<String> {

            val guessed = mutableListOf<String>()
            guessed.add(song.artist)
            guessed.add(song.title)
            val not = mutableListOf<String>()
            var chosen = mutableListOf<String>()
            not.add("???")
            not.add("???")

            val rand = Random()
            val ret = rand.nextInt(2)
            Log.d("rand", ret.toString())

            when(ret){
                0 -> chosen = guessed
                1 -> chosen =  not
            }

            return chosen



        }


        fun bind(song:Song)  {
            with(song) {
                val guessed = pref.getBoolean("Song ${song.number} guessed", false)
                if (guessed){
                    itemView.songTitle.text = song.title
                    itemView.songArtist.text = song.artist
                    itemView.song_icon.setImageResource(R.drawable.ic_music_note_black_24dp)
                    //itemView.song_icon.setColorFilter(dummyGuessed())

                }
                if (!guessed){
                    itemView.songTitle.text = "???"
                    itemView.songArtist.text = "???"
                    itemView.song_icon.setImageResource(R.drawable.ic_lock_outline_black_24dp)

                }

                itemView.collected_placemarks.text = dummyPlacemarks()
                itemView.setOnClickListener{mClick(this)}
            }

        }
    }

}