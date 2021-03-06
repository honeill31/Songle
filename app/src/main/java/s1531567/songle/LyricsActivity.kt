package s1531567.songle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kotlinx.android.synthetic.main.activity_lyrics.*
import kotlinx.android.synthetic.main.content_lyrics.*

/* This class displays the correct collected words in the form of a lyric sheet. */
class LyricsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val bar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(bar)
        supportActionBar!!.title = "Lyrics"

        val extras = intent.extras
        val song = extras.getInt("Song number", 1)
        //val map = extras.getInt("Map number", 1)
        var songLyrics = DownloadLyricTask(song).execute().get()
        val parser = LyricParser()
        val remove = parser.displayPlacemarkInLyrics(songLyrics, song, 1)
        lyrics.text = remove
        setSupportActionBar(toolbar)
        val fab = findViewById<FabSpeedDial>(R.id.fab_speed)
        fab.setMenuListener(object : SimpleMenuListenerAdapter(){
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                val map = menuItem!!.titleCondensed
                val id = map.toString().toInt()
                lyrics.text = parser.displayPlacemarkInLyrics(songLyrics,song, id)
                return true
            }
        })


    }
}
