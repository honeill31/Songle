package s1531567.songle

import android.content.SharedPreferences
import android.util.Log
import com.google.maps.android.data.kml.KmlPlacemark

/**
 * Created by s1531567 on 03/11/17.
 */
//given a placemark this class will return the lyric associated
class LyricParser() {

    fun findLyric(song: Int, map: Int, lyrics: String, placemark: KmlPlacemark) : Lyric {
        val name = placemark.getProperty("name")
        var result = Lyric(0,0,"")
        val pm = name.split(":")
        val lineNum = pm[0].toInt()
        val wordNum = pm[1].toInt()
        Log.v("linenum", lineNum.toString())
        Log.v("wordnum:", wordNum.toString())
        var editedLyrics : String = lyrics
        val remove = "[,|?|!|(|)|.]".toRegex()
        editedLyrics = lyrics.replace(remove, "") //removing unnecessary punctuation
        val lines : List<String> = editedLyrics.split("\n") //splitting into string array)
        val l = lines.map { value -> value.trim() } //removing whitespace from beginning and end of line

        Log.v("lyrics", l.toString())

        for (line in l) {
            val words = line.split(" ", "\t") //splitting on whitespace and tabs
            if (words[0].toInt()==(lineNum)){
                result = Lyric(lineNum, wordNum, words[wordNum])
                break
            }
            else {
                println("Wrong Line")
            }
        }
        return result
    }

    fun editedLyrics(lyrics: String) : List<String>{
        val editedLyrics = lyrics.split("\n") //splitting into string array)
        val l = editedLyrics.map { value -> value.trim() } //removing whitespace from beginning and end
        return l
    }

    fun intToString(num : Int) : String {
        var str = ""
        if (num <=9){
            str = "0$num"
        }
        else {
            str = "$num"
        }
        return str

    }

    fun displayPlacemarkInLyrics(lyrics: String, songNum : Int, mapNum: Int, pref: SharedPreferences) : String {

        var editedLyrics : String = lyrics
        val remove = "[,|?|!|(|)|.]".toRegex()
        editedLyrics = lyrics.replace(remove, "") //removing unnecessary punctuation
        val lines : List<String> = editedLyrics.split("\n") //splitting into string array)
        val l = lines.map { value -> value.trim() } //removing whitespace from beginning and end of line
        var result = ""

        for (line in 0..lines.size-1){
            val words = l[line].split(" ", "\t")
            println("Line $line: ${lines[line]}")
            for (word in 0..words.size-1){
                var collected = pref.getBoolean("$songNum $mapNum ${line+1} ${word+1}", false)
                println("Key: $songNum $mapNum ${line + 1} ${word + 1}, collected? $collected")
                if (collected){
                    result += "${words[word+1]} "
                }
                if (!collected){
                    result += "_"

                }
            }
            result += "\n"
        }
        return result
    }

}