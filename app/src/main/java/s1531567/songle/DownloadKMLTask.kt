package s1531567.songle

import android.content.Context
import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.kml.KmlLayer
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by s1531567 on 28/10/17.
 */
class DownloadKMLTask () : AsyncTask<String,Int,InputStream>() {

    override fun doInBackground(vararg params: String): InputStream {
        val stream = downloadUrl(params[0])
        return stream
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