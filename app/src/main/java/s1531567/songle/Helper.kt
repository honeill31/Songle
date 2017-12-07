package s1531567.songle

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.view.View
import org.jetbrains.anko.connectivityManager
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by holly on 04/12/17.
 */
class Helper {

    fun intToString(num : Int) : String {
        var str = ""
        if (num <=9){
            str = "0$num"
        }
        else {
            str = "$num"
        }
        return str

    }

    fun rand(from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
    }

    fun stringToInt(num : String) : Int {
        return num.toInt()

    }
    fun checkInternet(connectivityManager: ConnectivityManager) : Boolean {
        var connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED)

        return connected
    }

    fun refreshView(view : View){
        view.visibility = View.GONE
        view.visibility = View.VISIBLE
    }

    inner class DownloadStampTask(private val caller: DownloadCompleteListener) : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg urls: String): String {
            val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml"
            return loadXMLFromNetwork(u)


        }

        private fun loadXMLFromNetwork(urlString: String): String {
            return XmlParser().getStamp(downloadUrl(urlString))

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



        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            caller.downloadComplete(result)


        }

    }
}