package s1531567.songle

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by holly on 04/12/17.
 */
class Prefs(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)

    var currentSong : Int
        get() = prefs.getInt("Current Song", 1)
        set(value) = prefs.edit().putInt("Current Song", value).apply()

    var currentMap : Int
        get() = prefs.getInt("Current Map", 1)
        set(value) = prefs.edit().putInt("Current Map", value).apply()


}