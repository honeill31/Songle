package s1531567.songle

import android.app.Application

/**
 * Created by holly on 04/12/17.
 */

val prefs: Prefs by lazy {
    App.prefs!!
}

class App : Application() {
    companion object {
        var prefs: Prefs? = null
    }


    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}