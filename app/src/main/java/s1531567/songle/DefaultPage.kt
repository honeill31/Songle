package s1531567.songle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.URL

import kotlinx.android.synthetic.main.activity_default.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.URLConnection

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
                val task = DownloadXMLTask("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                task.execute()
            } else {
                ActivityCompat.requestPermissions(
                        this@DefaultPage,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE)
            }


        }
    }




}







