package s1531567.songle

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by holly on 04/12/17.
 */
class Prefs(context: Context) {
    val sharedPrefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()

    var currentSong : Int
        get() = sharedPrefs.getInt("Current Song", 1)
        set(value) = sharedPrefs.edit().putInt("Current Song", value).apply()

    var currentMap : Int
        get() = sharedPrefs.getInt("Current Map", 1)
        set(value) = sharedPrefs.edit().putInt("Current Map", value).apply()

    var songles : Int
        get() = sharedPrefs.getInt("songles",0)
        set(value) = sharedPrefs.edit().putInt("songles", value).apply()

    var points : Int
        get() = sharedPrefs.getInt("points",0)
        set(value) = sharedPrefs.edit().putInt("points", value).apply()

    var steps : Int
        get() = sharedPrefs.getInt("steps", 0)
        set(value) = sharedPrefs.edit().putInt("steps", value).apply()

    //function to check whether a word in a given song has been collected.
    fun collectedPrev(line: Int, w : Int) : Boolean {
        return sharedPrefs.getBoolean("$currentSong $currentMap $line $w", false)
    }

    fun setCollected (line: Int, w: Int) {
        editor.putBoolean("$currentSong $currentMap $line $w", true)//setting this word to collected
        editor.apply()
    }

    fun wordsCollected () : Int {
        return sharedPrefs.getInt("$currentSong $currentMap words collected", 0)
    }

    fun setWordsCollected (value: Int) {
        editor.putInt("$currentSong $currentMap words collected", value)
        editor.apply()
    }

    fun songGuessed () : Boolean {
        return sharedPrefs.getBoolean("Song $currentSong guessed", false)
    }

    fun songLocked () : Boolean {
        return sharedPrefs!!.getBoolean("Song $currentSong locked", true)
    }

    fun setSongGuessed () {
        editor.putBoolean("Song $currentSong guessed", true)
        editor.apply()
    }

    fun getTries () : Int {
        return sharedPrefs.getInt("Tries $currentSong", 3)
    }

    fun setTries (tries : Int ){
        editor.putInt("Tries $currentSong", tries)
        editor.apply()
    }





}