package s1531567.songle

import android.Manifest
import android.content.Context
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.TextView


import kotlinx.android.synthetic.main.activity_default.*
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
import java.io.File


class DefaultPage : AppCompatActivity() {
        val WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)


        play.setOnClickListener {
            val play = Intent(this, MapsActivity::class.java)
            startActivity(play)
        }


        songref.setOnClickListener {
            val lead = Intent(this@DefaultPage, SongList::class.java)
            startActivity(lead)
        }

        userprofile.setOnClickListener {
            startActivity(Intent(this@DefaultPage, UserActivity::class.java))
        }


        update.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val dialog = progressDialog("Please be patient", "Getting funky" )
                val task = DownloadXMLTask()
                task.execute()
                val songs = task.get()
                //File("Songs.txt").bufferedWriter().use { out -> out.write(songs)}
                Log.v("SONG", songs.toString())
            } else {
                ActivityCompat.requestPermissions(
                        this@DefaultPage,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE)
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
        val steps = prefs.sharedPrefs.getInt("steps", 0)
        var txt = findViewById<TextView>(R.id.textView)
        txt.text = "Total number of steps walked: $steps"
    }




}







