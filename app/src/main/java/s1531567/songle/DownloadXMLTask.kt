package s1531567.songle

import android.content.res.Resources
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

/**
 * Created by s1531567 on 17/10/17.
 */
class DownloadXMLTask () :
        AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg urls: String): String {
            val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml"
            return try {
                loadXMLFromNetwork(u)
            } catch (e: IOException) {
                "Unable to load content, check your network connection."
            } catch (e: XmlPullParserException) {
                "Error parsing XML"
            }

        }

        private fun loadXMLFromNetwork(urlString: String): String {
            val parser = XmlParser()
            val stream = downloadUrl(urlString)
            val result = parser.parse(stream)

            return result.toString()
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


        }

}