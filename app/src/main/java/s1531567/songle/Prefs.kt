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
    fun collectedPrev(song: Int, map: Int, line: Int, w: Int) : Boolean {
        return sharedPrefs.getBoolean("$song $map $line $w", false)
    }

    fun setCollected (song: Int, map: Int, line: Int, w: Int) {
        editor.putBoolean("$song $map $line $w", true)//setting this word to collected
        editor.apply()
    }

    fun wordsCollected (song: Int, map: Int) : Int {
        return sharedPrefs.getInt("$song $map words collected", 0)
    }

    fun setWordsCollected (song: Int, map: Int, value: Int) {
        editor.putInt("$song $map words collected", value)
        editor.apply()
    }

    fun songGuessed (song: Int) : Boolean {
        return sharedPrefs.getBoolean("Song $song guessed", false)
    }

    fun setSongGuessed (song: Int) {
        editor.putBoolean("Song $song guessed", true)
        editor.apply()
    }

    fun songLocked (song: Int) : Boolean {
        return sharedPrefs.getBoolean("Song $song locked", true)
    }

    fun setSongUnlocked (song: Int){
        editor.putBoolean("Song $song locked", false)
        editor.apply()
    }

    fun mapLocked (song: Int, map: Int) : Boolean {
        return sharedPrefs.getBoolean("Song $song Map $map locked", true)
    }

    fun setMapUnlocked (song : Int, map: Int){
        editor.putBoolean("Song $song Map $map locked", false)
        editor.apply()
    }

    fun getTries (song : Int) : Int {
        return sharedPrefs.getInt("Tries $song", 3)
    }

    fun setTries (song: Int, tries : Int){
        editor.putInt("Tries $song", tries)
        editor.apply()
    }

    fun getScoreMode (song: Int) : Int {
        return sharedPrefs.getInt("Song $song score mode", 1)
    }

    fun getPlacemarkTotal (song: Int) : Int {
        return sharedPrefs.getInt("Song $song total Placemarks", 0)
    }

    fun getMapPlacemarkTotal (song: Int, map: Int) : Int {
        return sharedPrefs.getInt("Song $song Map $map Placemarks", 0)
    }

    fun getMapCollected (song: Int, map: Int) : Int {
        return sharedPrefs.getInt("$song $map words collected", 0)
    }

    fun changeScoreMode (song: Int) {
        editor.putBoolean("Song $song given up", true)
        editor.putInt("Song $song score mode", 2)
        editor.apply()
    }




    /* ----------------- Achievements ------------------ */

    fun checkAchievement (id : Int) : Boolean {
        return sharedPrefs.getBoolean("Achievement $id locked", true)
    }

    fun setAchievementUnlocked (id: Int) {
        editor.putBoolean("Achievement $id locked", false)
        editor.apply()
    }





}