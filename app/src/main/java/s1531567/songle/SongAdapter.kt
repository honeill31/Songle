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
import org.jetbrains.anko.toast

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


        fun totalPlacemarks(songNum : Int) : String {
            var collected = 0

            for (i in 1..5){
                //change to for in in 1 to unlocked
                val songMap = "$songNum $i"
                collected += prefs.sharedPrefs.getInt("$songMap words collected",0 )
                Log.v("collected and pos", collected.toString() + " " + i.toString())

            }


            val total = prefs.sharedPrefs.getInt("Song $songNum total Placemarks", 0)
            return "$collected/$total"

        }

        fun stringToInt(num : String) : Int {
//            var str = 0
//            Log.v("num to int", num.toInt().toString())
//            Log.v("num", num)
//            if (num.toInt() > 9){
//                str = num.toInt()
//            }
//            else {
//                Log.v("num[0]", num[0].toString())
//                Log.v("num[1]", num[1].toString())
//                Log.v("str", str.toString())
//
//                str = num[1].toString().toInt()
//            }
//            return str
            return num.toInt()

        }

        fun bind(song:Song)  {
            with(song) {
                val guessed = prefs.sharedPrefs.getBoolean("Song ${stringToInt(song.number)} guessed", false)
                val locked = prefs.sharedPrefs.getBoolean("Song ${stringToInt(song.number)} locked", true)
                if (guessed){
                    itemView.songTitle.text = song.title
                    itemView.songArtist.text = song.artist

                }
                if (!guessed){
                    itemView.songTitle.text = "???"
                    itemView.songArtist.text = "???"


                }
                if (locked){
                    itemView.song_icon.setImageResource(R.drawable.ic_lock_outline_black_24dp)
                    itemView.collected_placemarks.text = ""
                }
                if (!locked){
                    itemView.song_icon.setImageResource(R.drawable.ic_music_note_black_24dp)
                    itemView.collected_placemarks.text = totalPlacemarks(stringToInt(song.number))

                }
                itemView.levelNumber.text = stringToInt(song.number).toString()

                Log.v("Song", song.toString())
                Log.v("string to int", "${stringToInt(song.number)}")


                itemView.setOnClickListener{mClick(this)}
            }

        }
    }

}