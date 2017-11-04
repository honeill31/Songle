package s1531567.songle

import android.util.Log
import com.google.maps.android.data.kml.KmlPlacemark

/**
 * Created by s1531567 on 03/11/17.
 */
//given a placemark this class will return the lyric associated
class LyricParser() {

    fun findLyrics(song: Int, map: Int, lyrics: String, placemark: KmlPlacemark) : Lyric {
        var result = Lyric(0,0,"")
        val name = placemark.getProperty("name")
        val words = name.split(":")
        val lineNum = words[0].toInt()
        val wordNum = words[1].toInt()
        var editedLyrics = lyrics
        val remove = "[,|?|!|(|)|.|]".toRegex()
        Log.v("name", name)
        Log.v("words", words.toString())
        Log.v("line", lineNum.toString())
        Log.v("word", wordNum.toString())
        editedLyrics = lyrics.replace(remove, "")
        //removing unnecessary punctuation

        val lyric = editedLyrics.split("\n") //splitting into string array
        Log.v("lyric", editedLyrics.toString())
        println(editedLyrics.)

        for (line in editedLyrics){
            println(line.substring(0,1))
            if (line.substring(0,1).toInt() == lineNum ){
                val lineWords = line.split(" ")
                val result = Lyric(lineNum, wordNum, lineWords[wordNum])
            }
            else {
                println("Incorrect Line")
            }

        }
        return result



    }

}