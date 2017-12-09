package s1531567.songle

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.songs_layout.view.*

/**
 * Created by holly on 07/11/17.
 */

/* Handles the display of Songs in SongList */
class SongAdapter(val context: Context, val songs: List<Song>, private val itemClick : (Song)->Unit) : RecyclerView.Adapter<SongAdapter.SongHolder>() {


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


    class SongHolder(view: View, itemClick: (Song) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mClick = itemClick


        private fun totalPlacemarks(songNum : Int) : String {
            var collected = 0

            for (i in 1..5){
                //change to for in in 1 to unlocked
                collected += prefs.wordsCollected(songNum, i)
                Log.v("collected and pos", collected.toString() + " " + i.toString())

            }
            val total = prefs.getPlacemarkTotal(songNum)
            return "$collected/$total"

        }

        fun isWhitespace(char: Char) : Char {
            if (char == ' '){
                return ' '
            }
            else return '?'
        }


        fun bind(song:Song)  {
            with(song) {
                val guessed = prefs.songGuessed(Helper().stringToInt(song.number))
                val locked = prefs.songLocked(Helper().stringToInt(song.number))
                if (guessed){
                    itemView.songTitle.text = song.title
                    itemView.songArtist.text = song.artist

                }
                if (!guessed){
                    itemView.songTitle.text = "???" //song.title.map { it -> isWhitespace(it) }.joinToString(" ")
                    itemView.songArtist.text = "???"//song.artist.map { it -> isWhitespace(it) }.joinToString(" ")


                }
                if (locked){
                    itemView.song_icon.setImageResource(R.drawable.ic_lock_outline_black_24dp)
                    itemView.collected_placemarks.text = ""
                }
                if (!locked){
                    itemView.song_icon.setImageResource(R.drawable.ic_music_note_black_24dp)
                    itemView.collected_placemarks.text = totalPlacemarks(Helper().stringToInt(song.number))

                }
                itemView.levelNumber.text = Helper().stringToInt(song.number).toString()

                //Log.v("Song", song.toString())
                //Log.v("string to int", "${Helper().stringToInt(song.number)}")


                itemView.setOnClickListener{mClick(this)}
            }

        }
    }

}