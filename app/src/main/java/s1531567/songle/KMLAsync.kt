package s1531567.songle

import android.location.Location
import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by holly on 29/10/17.
 */
class KMLAsync() : AsyncTask<String, Int, List<Placemark>>() {

    override fun doInBackground(vararg params: String): List<Placemark> {
        val stream = downloadUrl(params[0])
        return getPlacemarks(stream)
    }

    fun getPlacemarks(stream: InputStream) : List<Placemark> {
        val parser = KMLParser()
        val placemarks = parser.parse(stream)

        return placemarks


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