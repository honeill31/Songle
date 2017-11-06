package s1531567.songle

import android.app.ActionBar
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_song_list.*
import org.jetbrains.anko.progressDialog
import java.security.KeyStore

class SongList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)
        val mListView = findViewById<ListView>(R.id.songlist)
        val dialog = progressDialog(message = "Please wait a bit…", title = "Fetching songs")

        }

    override fun onResume() {
        super.onResume()
        val task = DownloadXMLTask()
        task.execute()
        val songs = task.get()
        val dialog = progressDialog(message = "Please wait a bit…", title = "Fetching songs")
        dialog.isIndeterminate = true

    }


    }



