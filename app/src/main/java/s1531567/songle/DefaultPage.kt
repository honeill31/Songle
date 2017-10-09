package s1531567.songle

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
import java.util.jar.Manifest

class DefaultPage : AppCompatActivity() {
        val WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        val permissionCheck = ContextCompat.checkSelfPermission(
                this@DefaultPage,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)


        leader.setOnClickListener {
            val lead = Intent(this@DefaultPage, LeaderboardActivity::class.java)
            startActivity(lead)
        }

        update.setOnClickListener {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this@DefaultPage,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE)
            }
            val task = UpdateTask()
            task.execute()
        }
    }

    inner class UpdateTask() : AsyncTask<String,Int,String>() {


        override fun onPreExecute() {
            super.onPreExecute()
            print("Starting Download")

        }

        override fun doInBackground(vararg p0: String?): String {
            var count : Int = 0
            try {
                val url = java.net.URL("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                val connection : URLConnection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val input : InputStream = BufferedInputStream(url.openStream(), 8192)
                val output : OutputStream = FileOutputStream(Environment.getExternalStorageDirectory().toString() + "songs.xml")

                val data : ByteArray = byteArrayOf(1024.toByte())
                var total : Long = 0

                while (count != -1){
                    total += count
                    //publishProgress((total*100/lengthOfFile).toInt())
                    output.write(data, 0, count )
                }
                output.flush()
                output.close()
                input.close()

            } catch (e: Exception){
                Log.e("Error", e.message)
            }
            return ""


        }

        override fun onPostExecute(result: String?) {

        }
    }


}







