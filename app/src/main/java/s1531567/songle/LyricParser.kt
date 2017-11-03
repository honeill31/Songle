package s1531567.songle

import com.google.maps.android.data.kml.KmlPlacemark

/**
 * Created by s1531567 on 03/11/17.
 */
//given a placemark this class will return the lyric associated
class LyricParser() {

    fun findLyrics(song: Int, map: Int, lyrics: String, placemark: Placemark) : Lyric {
        var result = Lyric(0,0,"")
        val name = placemark.name
        val words = name.split(":")
        val lineNum = words[0].toInt()
        val wordNum = words[1].toInt()
        lyrics.map { lyrics.replace(",", "") }
        lyrics.map { lyrics.replace("?", "") }
        lyrics.map { lyrics.replace("!", "") }
        lyrics.map { lyrics.replace("(", "") }
        lyrics.map { lyrics.replace(")", "") }
        lyrics.map { lyrics.replace(".", "") }
        //removing unnecessary punctuation
        val lyric = lyrics.split("\n") //splitting into string array

        for (line in lyric){
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