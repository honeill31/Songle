package s1531567.songle

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.maps.android.data.kml.KmlLayer
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

        fun totalPlacemarks(songNum : Int) : String {
            val rand = Random()
            var collected = 0

            for (i in 1..5){
                val songMap = "$songNum $i"
                collected += pref.getInt("$songMap words collected",0 )

            }


            val total = pref.getInt("Song $songNum total Placemarks", 0)
            return "$collected/$total"

        }
        fun totalGuessed(): Int {
            val guessed = 10000
            val notguessed = 22222

            return guessed or notguessed


        }

        fun stringToInt(num : String) : Int {
            var str = 0
            if (num.toInt() >9){
                str = num.toInt()
            }
            else {
                str = num[1].toInt()
            }
            return str

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

                itemView.collected_placemarks.text = totalPlacemarks(stringToInt(song.number))
                itemView.setOnClickListener{mClick(this)}
            }

        }
    }

}