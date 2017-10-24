package s1531567.songle

import android.os.AsyncTask
import com.google.maps.android.data.kml.KmlLayer
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by holly on 24/10/17.
 */
class DownloadKMLTask(vararg url: String) : AsyncTask<String,Int,InputStream>() {
    private val mUrl = url

    override fun doInBackground(vararg params: String): InputStream {
        return downloadUrl(mUrl[0])
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

    override fun onPostExecute(result: InputStream) {
        super.onPostExecute(result)
    }
}