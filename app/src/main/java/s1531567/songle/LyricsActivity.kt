package s1531567.songle

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lyrics.*
import kotlinx.android.synthetic.main.content_lyrics.*

class LyricsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        val extras = intent.extras
        val song = extras.getInt("Song number", 1)
        val songLyrics = DownloadLyricTask(song).get()
        //lyrics.text = songLyrics
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
