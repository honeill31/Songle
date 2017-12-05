package s1531567.songle

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.maps.android.data.kml.KmlLayer
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.maps.GoogleMap

/**
 * Created by holly on 24/10/17.
 */
class KMLtask(songNum : Int, mapNum : Int) : AsyncTask<String,Int,List<Placemark>>() {
    val mSongNum = songNum
    val mMapNum = mapNum

    override fun doInBackground(vararg params: String): List<Placemark> {
        val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/${Helper().intToString(mSongNum)}/map$mMapNum.txt"
        Log.v("doInBG Value", u)
        return loadKMLFromNetwork(u)
    }

    private fun loadKMLFromNetwork(urlString: String): List<Placemark> {
        val parser = KmlParser()
        val stream = downloadUrl(urlString)
        Log.v("stream", stream.toString())
        return  parser.parse(stream)

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

    override fun onPostExecute(result: List<Placemark>) {
        super.onPostExecute(result)
    }
}