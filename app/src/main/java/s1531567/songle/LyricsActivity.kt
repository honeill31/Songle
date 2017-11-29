package s1531567.songle

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_lyrics.*
import kotlinx.android.synthetic.main.content_lyrics.*

class LyricsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val extras = intent.extras
        val song = extras.getInt("Song number", 1)
        //val map = extras.getInt("Map number", 1)
        var songLyrics = DownloadLyricTask(song).execute().get()
        val parser = LyricParser()
        val remove = parser.displayPlacemarkInLyrics(songLyrics, song, 1, pref)
        Log.v("removw", remove)
        lyrics.text = remove
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
