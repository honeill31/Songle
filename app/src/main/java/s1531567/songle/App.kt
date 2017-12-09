package s1531567.songle
import android.app.Application
import android.util.Log

/**
 * Created by holly on 04/12/17.
 */

/* Extension of Application to make preferences easier to manage */

val prefs: Prefs by lazy {
    App.prefs!!
}


class App : Application() {
    companion object {
        var prefs: Prefs? = null
    }




    override fun onCreate() {
        Log.v("Inside App onCreate", "")
        prefs = Prefs(applicationContext)
        Log.v("Prefs value, (App)", prefs.toString())
        super.onCreate()
    }
}