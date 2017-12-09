package s1531567.songle


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_default.*
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.toast


class DefaultPage : AppCompatActivity() {
    private val PERMISSION_ACCESS_FINE_LOCATION = 1
    companion object : DownloadCompleteListener{
        override fun downloadComplete(result: Any) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        // Checks whether there is a user currently logged in.
        if (!prefs.loggedIn){
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Check if accessing the user's location is allowed, and letting them play Songle if so/
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)



        play.setOnClickListener {
            val play = Intent(this, MapsActivity::class.java)
            if (Helper().checkInternet(connectivityManager)){
                if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSION_ACCESS_FINE_LOCATION)
                    toast("You must enable Location to play Songle")

                }
                if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                    startActivity(play)
                }

            }
            else toast("You must have an internet connection to play Songle")

        }


        songref.setOnClickListener {
            val song = Intent(this@DefaultPage, SongList::class.java)
            startActivity(song)
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

        help.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }
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







