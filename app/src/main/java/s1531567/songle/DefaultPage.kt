package s1531567.songle

import android.Manifest
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.maps.android.data.Geometry
import com.google.maps.android.data.kml.KmlPlacemark


import kotlinx.android.synthetic.main.activity_default.*
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


        leader.setOnClickListener {
            val lead = Intent(this@DefaultPage, LeaderboardActivity::class.java)
            startActivity(lead)
        }

        update.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val task = DownloadXMLTask()
                task.execute()
                val songs = task.get()
                //File("Songs.txt").bufferedWriter().use { out -> out.write(songs)}

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




}







