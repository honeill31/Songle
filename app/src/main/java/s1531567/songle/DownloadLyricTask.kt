package s1531567.songle

import android.os.AsyncTask
import com.google.maps.android.data.kml.KmlPlacemark
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by s1531567 on 03/11/17.
 */
class DownloadLyricTask(val song : Int) : AsyncTask<String, Int, String>(){

    override fun doInBackground(vararg p0: String?): String {
        val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/${Helper().intToString(song)}/words.txt"
        return downloadUrl(url).bufferedReader().use { it.readText() }



    }


    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection

        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true

        conn.connect()
        return conn.inputStream
    }

}