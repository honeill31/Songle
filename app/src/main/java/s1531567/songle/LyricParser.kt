package s1531567.songle

/**
 * Created by s1531567 on 03/11/17.
 */
//given a placemark this class will return the lyric associated
class LyricParser() {

    fun findLyric(lyrics: String, placemark: Placemark) : Lyric {

        var result = Lyric(0,0,"")
        val pm = placemark.name.split(":")
        val line = pm[0].toInt()
        val word = pm[1].toInt()
        var editedLyrics : String
        val remove = "[,|?|!|(|)|.]".toRegex()


        editedLyrics = lyrics.replace(remove, "")
        val lines : List<String> = editedLyrics.split("\n")
        val l = lines.map { it -> it.trim() }
        val correctLine = l.map { ((it.split(" ", "\t"))) }.firstOrNull { it[0].toInt() == line }
        result = Lyric(line, word, correctLine!![word])
        return result
    }


    /* This function is used to find the correct word to display in the lyrics activity,
     * only showing words they have collected */
    fun displayPlacemarkInLyrics(lyrics: String, songNum : Int, mapNum: Int) : String {

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
                var collected = prefs.collectedPrev(songNum, mapNum, (line+1), (word+1))
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