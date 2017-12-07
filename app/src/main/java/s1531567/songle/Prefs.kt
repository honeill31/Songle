package s1531567.songle

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by holly on 04/12/17.
 */
class Prefs(context: Context) {

    /*----------------- Variables to be passed between activities ------------------ */

    val gamePrefs : SharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)

    var users : String
        get() = gamePrefs.getString("User info","")
        set(value) = gamePrefs.edit().putString("User info", value).apply()

    var currentUser : String
        get() = gamePrefs.getString("current user", "")
        set(value) = gamePrefs.edit().putString("current user", value).apply()


    val userPrefs: SharedPreferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE)
    val editor = userPrefs.edit()


    var update : Boolean
        get() = userPrefs.getBoolean("update", true)
        set(value) = userPrefs.edit().putBoolean("update", false).apply()

    var currentSong : Int
        get() = userPrefs.getInt("Current Song", 1)
        set(value) = userPrefs.edit().putInt("Current Song", value).apply()

    var currentMap : Int
        get() = userPrefs.getInt("Current Map", 1)
        set(value) = userPrefs.edit().putInt("Current Map", value).apply()

    var songles : Int
        get() = userPrefs.getInt("songles",0)
        set(value) = userPrefs.edit().putInt("songles", value).apply()

    var points : Int
        get() = userPrefs.getInt("points",0)
        set(value) = userPrefs.edit().putInt("points", value).apply()

    var steps : Int
        get() = userPrefs.getInt("steps", 0)
        set(value) = userPrefs.edit().putInt("steps", value).apply()

    var totalScore : Int
        get() = userPrefs.getInt("total score", 0)
        set(value) = userPrefs.edit().putInt("total score", value).apply()

    var timeStamp : String
        get() = userPrefs.getString("timestamp", "")
        set(value) = userPrefs.edit().putString("timestamp", value).apply()

    var songTotal : Int
        get() = userPrefs.getInt("Song Count", 1)
        set(value) = userPrefs.edit().putInt("Song Count", value).apply()


/* ------------------------- Song Functions --------------------------- */

    fun collectedPrev(song: Int, map: Int, line: Int, w: Int) : Boolean {
        return userPrefs.getBoolean("$song $map $line $w", false)
    }

    fun setCollected (song: Int, map: Int, line: Int, w: Int) {
        editor.putBoolean("$song $map $line $w", true)//setting this word to collected
        editor.apply()
    }

    fun wordsCollected (song: Int, map: Int) : Int {
        return userPrefs.getInt("$song $map words collected", 0)
    }

    fun setWordsCollected (song: Int, map: Int, value: Int) {
        editor.putInt("$song $map words collected", value)
        editor.apply()
    }

    fun songGuessed (song: Int) : Boolean {
        return userPrefs.getBoolean("Song $song guessed", false)
    }

    fun setSongGuessed (song: Int) {
        editor.putBoolean("Song $song guessed", true)
        editor.apply()
    }

    fun songLocked (song: Int) : Boolean {
        return userPrefs.getBoolean("Song $song locked", true)
    }

    fun setSongUnlocked (song: Int){
        editor.putBoolean("Song $song locked", false)
        editor.apply()
    }

    fun mapLocked (song: Int, map: Int) : Boolean {
        return userPrefs.getBoolean("Song $song Map $map locked", true)
    }

    fun setMapUnlocked (song : Int, map: Int){
        editor.putBoolean("Song $song Map $map locked", false)
        editor.apply()
    }

    fun getTries (song : Int) : Int {
        return userPrefs.getInt("Tries $song", 3)
    }

    fun setTries (song: Int, tries : Int){
        editor.putInt("Tries $song", tries)
        editor.apply()
    }

    fun getScoreMode (song: Int) : Int {
        return userPrefs.getInt("Song $song score mode", 1)
    }

    fun score1Total (song: Int, value: Int) {
        editor.putInt("Song $song score 1", value)
        editor.apply()
    }

    fun score2Total (song: Int, value: Int) {
        editor.putInt("Song $song score 2", value)
        editor.apply()
    }

    fun songScore (song: Int) : Int {
        return userPrefs.getInt("Song $song total score", 0)
    }

    fun setSongScore (song: Int, value: Int) {

    }

    fun getPlacemarkTotal (song: Int) : Int {
        return userPrefs.getInt("Song $song total Placemarks", 0)
    }

    fun getMapPlacemarkTotal (song: Int, map: Int) : Int {
        return userPrefs.getInt("Song $song Map $map Placemarks", 1) //avoid division by 0
    }

    fun getMapCollected (song: Int, map: Int) : Int {
        return userPrefs.getInt("$song $map words collected", 0)
    }

    fun changeScoreMode (song: Int) {
        editor.putBoolean("Song $song given up", true)
        editor.putInt("Song $song score mode", 2)
        editor.apply()
    }

    fun givenUp (song: Int) : Boolean {
        return userPrefs.getBoolean("Song $song given up", false)
    }




    /* ----------------- Achievements ------------------ */

    fun checkAchievement (id : Int) : Boolean {
        return userPrefs.getBoolean("Achievement $id locked", true)
    }

    fun setAchievementUnlocked (id: Int) {
        editor.putBoolean("Achievement $id locked", false)
        editor.apply()
    }





}