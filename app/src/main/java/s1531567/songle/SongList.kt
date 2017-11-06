package s1531567.songle

import android.app.ActionBar
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
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
        val mRecyclerView = findViewById<RecyclerView>(R.id.songlist)
        val task = DownloadXMLTask()
        task.execute()
        val dialog = progressDialog(message = "Please wait a bit…", title = "Fetching songs")
        val songs = task.get()
        dialog.max = songs.size



        }

    override fun onResume() {
        super.onResume()



    }


    }



