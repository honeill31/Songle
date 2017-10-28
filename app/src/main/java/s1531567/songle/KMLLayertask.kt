package s1531567.songle

import android.content.Context
import android.os.AsyncTask
import com.google.maps.android.data.kml.KmlLayer
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.maps.GoogleMap

/**
 * Created by holly on 24/10/17.
 */
class KMLLayertask(map: GoogleMap, context : Context) : AsyncTask<String,Int,KmlLayer>() {
    private val mMap = map
    private  val mContext = context

    override fun doInBackground(vararg params: String): KmlLayer {
        val stream =  downloadUrl(params[0])
        return KmlLayer(mMap, stream, mContext)
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

    override fun onPostExecute(result: KmlLayer) {
        super.onPostExecute(result)
    }
}