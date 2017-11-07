package s1531567.songle

import android.app.ActionBar
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_song_list.*
import org.jetbrains.anko.progressDialog
import java.security.KeyStore

class SongList : AppCompatActivity() {

    private var myAdapter : SongAdapter? = null
    private var songList : List<Song>? = null
    private var layoutManager : RecyclerView.LayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        initialise()

        }

    fun initialise(){
        val task = DownloadXMLTask()
        task.execute()
        songList = task.get()
        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        songlist.layoutManager = layoutManager
        myAdapter = SongAdapter(songList!!, this, R.layout.songs_layout)
        songlist.adapter = myAdapter

    }



    override fun onResume() {
        super.onResume()



    }


    }



