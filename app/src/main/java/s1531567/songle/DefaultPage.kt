package s1531567.songle

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.File

import kotlinx.android.synthetic.main.activity_default.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class DefaultPage : AppCompatActivity() {
        val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)


        leader.setOnClickListener {
            val lead = Intent(this@DefaultPage, LeaderboardActivity::class.java)
            startActivity(lead)
        }

        update.setOnClickListener {
            val task = UpdateTask()
            task.execute()
        }
    }

    inner class UpdateTask() : AsyncTask<String,Int,String>() {
        val permissionCheck = ContextCompat.checkSelfPermission(
                this@DefaultPage,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        override fun onPreExecute() {
            progBar.max = 100
            progBar.visibility = View.VISIBLE
        }

        override fun onProgressUpdate(vararg values: Int?) {
            progBar.progress
        }

        override fun doInBackground(vararg p0: String?): String {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this@DefaultPage,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }

            var stream: InputStream? = null
            var Result: String = ""
            print("HI")

            try {
                val url = java.net.URL("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                stream = url.openStream()
                val br = stream.bufferedReader()

                do {
                    val line = br.readLine()
                    Result += line
                } while (line != null)

            } catch (e: Exception) {
                return ""
            } finally {
                try {
                    if (stream != null) stream.close()
                } catch (ioe: IOException) {
                }
            }

            val file = File( Environment.getExternalStorageDirectory(), "songs.xml")

            file.bufferedWriter().use { out -> out.write(Result) }
            return Result

        }

        override fun onPostExecute(result: String?) {
            progBar.progress = 100
            progBar.visibility = View.INVISIBLE
        }
    }


}







