package s1531567.songle

import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by holly on 04/12/17.
 */

/* This class was created to make the storage of persistent data easier. */
class Prefs(context: Context) {

    /*----------------- Variables to be passed between activities ------------------ */


    // Stores state of game across players
    val gamePrefs : SharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
    val gameEditor = gamePrefs.edit()

    // Stores user preferences
    val sharedPrefs : SharedPreferences = context.defaultSharedPreferences

    var lastLogin : String
        get() = gamePrefs.getString("$currentUser last login", "")
        set(value) = gamePrefs.edit().putString("$currentUser last login", value).apply()

    var loggedIn : Boolean
        get() = gamePrefs.getBoolean("logged in", false)
        set(value) = gamePrefs.edit().putBoolean("logged in", value).apply()

    var users : String
        get() = gamePrefs.getString("User info","")
        set(value) = gamePrefs.edit().putString("User info", value).apply()

    var currentUser : String
        get() = gamePrefs.getString("current user", "")
        set(value) = gamePrefs.edit().putString("current user", value).apply()

    var userName : String
        get() = gamePrefs.getString("Username", "")
        set(value) = gamePrefs.edit().putString("Username", value).apply()

    var timeStamp : String
        get() = gamePrefs.getString("timestamp", "")
        set(value) = gamePrefs.edit().putString("timestamp", value).apply()


    var update : Boolean
        get() = gamePrefs.getBoolean("$currentUser update", true)
        set(value) = gamePrefs.edit().putBoolean("$currentUser update", value).apply()

    var firstRun : Boolean
        get() = gamePrefs.getBoolean("$currentUser first run", true)
        set(value) = gamePrefs.edit().putBoolean("$currentUser first run", value).apply()


    var currentSong : Int
        get() =         gamePrefs.getInt("$currentUser Current Song", 1)
        set(value) = gamePrefs.edit().putInt("$currentUser Current Song", value).apply()

    var currentMap : Int
        get() =         gamePrefs.getInt("$currentUser Current Map", 1)
        set(value) = gamePrefs.edit().putInt("$currentUser Current Map", value).apply()

    var songles : Int
        get() =         gamePrefs.getInt("$currentUser songles",0)
        set(value) = gamePrefs.edit().putInt("$currentUser songles", value).apply()

    var points : Int
        get() =         gamePrefs.getInt("$currentUser points",0)
        set(value) = gamePrefs.edit().putInt("$currentUser points", value).apply()

    var steps : Int
        get() =         gamePrefs.getInt("$currentUser steps", 0)
        set(value) = gamePrefs.edit().putInt("$currentUser steps", value).apply()

    var totalScore : Int
        get() =         gamePrefs.getInt("$currentUser total score", 0)
        set(value) = gamePrefs.edit().putInt("$currentUser total score", value).apply()



/* ------------------------- Song Functions --------------------------- */

    var songTotal : Int
        get() =         gamePrefs.getInt("Song Count", 1)
        set(value) = gamePrefs.edit().putInt("Song Count", value).apply()

    fun collectedPrev(song: Int, map: Int, line: Int, w: Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser $song $map $line $w", false)
    }

    fun setCollected (song: Int, map: Int, line: Int, w: Int) {
        gameEditor.putBoolean("$currentUser $song $map $line $w", true)//setting this word to collected
        gameEditor.apply()
    }

    fun wordsCollected (song: Int, map: Int) : Int {
        return gamePrefs.getInt("$currentUser $song $map words collected", 0)
    }

    fun setWordsCollected (song: Int, map: Int, value: Int) {
        gameEditor.putInt("$currentUser $song $map words collected", value)
        gameEditor.apply()
    }

    fun songGuessed (song: Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser Song $song guessed", false)
    }

    fun setSongGuessed (song: Int) {
        gameEditor.putBoolean("$currentUser Song $song guessed", true)
        gameEditor.apply()
    }

    fun songLocked (song: Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser Song $song locked", true)
    }

    fun setSongUnlocked (song: Int){
        gameEditor.putBoolean("$currentUser Song $song locked", false)
        gameEditor.apply()
    }

    fun mapLocked (song: Int, map: Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser Song $song Map $map locked", true)
    }

    fun setMapUnlocked (song : Int, map: Int){
        gameEditor.putBoolean("$currentUser Song $song Map $map locked", false)
        gameEditor.apply()
    }

    fun getTries (song : Int) : Int {
        return gamePrefs.getInt("$currentUser Tries $song", 3)
    }

    fun setTries (song: Int, tries : Int){
        gameEditor.putInt("$currentUser Tries $song", tries)
        gameEditor.apply()
    }

    fun getScoreMode (song: Int) : Int {
        return gamePrefs.getInt("$currentUser Song $song score mode", 1)
    }

    fun score1Total (song: Int, value: Int) {
        gameEditor.putInt("$currentUser Song $song score 1", value)
        gameEditor.apply()
    }

    fun score2Total (song: Int, value: Int) {
        gameEditor.putInt("$currentUser Song $song score 2", value)
        gameEditor.apply()
    }

    fun songScore (song: Int) : Int {
        return gamePrefs.getInt("$currentUser Song $song total score", 0)
    }

    fun setSongScore (song: Int, value: Int) {
        gameEditor.putInt("$currentUser Song $song total score", value).apply()

    }

    fun getPlacemarkTotal (song: Int) : Int {
        return gamePrefs.getInt("Song $song total Placemarks", 0)
    }

    fun getMapPlacemarkTotal (song: Int, map: Int) : Int {
        return gamePrefs.getInt("Song $song Map $map Placemarks", 1) //avoid division by 0
    }

    fun getMapCollected (song: Int, map: Int) : Int {
        return gamePrefs.getInt("$currentUser $song $map words collected", 0)
    }

    fun changeScoreMode (song: Int) {
        gameEditor.putBoolean("$currentUser Song $song given up", true)
        gameEditor.putInt("$currentUser Song $song score mode", 2)
        gameEditor.apply()
    }

    fun givenUp (song: Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser Song $song given up", false)
    }




    /* ----------------- Achievements ------------------ */

    fun checkAchievement ( id : Int) : Boolean {
        return gamePrefs.getBoolean("$currentUser Achievement $id locked", true)
    }

    fun setAchievementUnlocked (id: Int) {
        gameEditor.putBoolean("$currentUser Achievement $id locked", false)
        gameEditor.apply()
    }





}