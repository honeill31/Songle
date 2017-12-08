package s1531567.songle

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView


import kotlinx.android.synthetic.main.activity_default.*
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.toast


class DefaultPage : AppCompatActivity() {
        val WRITE_EXTERNAL_STORAGE = 1
    companion object : DownloadCompleteListener{
        override fun downloadComplete(result: Any) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        prefs.gamePrefs.all.entries.map { println("gamePrefs + $it") }
        prefs.gamePrefs.all.entries.map { println("gamePrefs + $it") }
        println(prefs.gamePrefs.all.entries == prefs.gamePrefs.all.entries)



        play.setOnClickListener {
            if (Helper().checkInternet(connectivityManager)){
                val play = Intent(this, MapsActivity::class.java)
                startActivity(play)
            }
            else toast("You must have an internet connection to play Songle")

        }


        songref.setOnClickListener {
            val lead = Intent(this@DefaultPage, FaqActivity::class.java)
            startActivity(lead)
        }

        userprofile.setOnClickListener {
            startActivity(Intent(this@DefaultPage, UserActivity::class.java))
        }


        update.setOnClickListener {
            val stamp = Helper().DownloadStampTask(DefaultPage.Companion)
                        .execute()
                        .get()
            Log.v("timestamp", stamp)
            if (stamp != prefs.timeStamp){
                val songs = DownloadXMLTask(DefaultPage.Companion).execute().get()
                prefs.songTotal = songs.size
                prefs.timeStamp = stamp
                prefs.update = true
                Log.v("songs size", songs.size.toString())
            }




        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        img.setBackgroundResource(0)
    }

    override fun onResume() {
        super.onResume()
        val steps = prefs.gamePrefs.getInt("steps", 0)
        var txt = findViewById<TextView>(R.id.textView)
        txt.text = "Total number of steps walked: $steps"
    }




}







