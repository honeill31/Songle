package s1531567.songle


import android.os.AsyncTask
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by s1531567 on 17/10/17.
 */
class DownloadXMLTask () : AsyncTask<String, Int, List<Song>>() {

        override fun doInBackground(vararg urls: String): List<Song> {
            val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml"
            return loadXMLFromNetwork(u)


        }

        private fun loadXMLFromNetwork(urlString: String): List<Song> {
            val parser = XmlParser()
            val stream = downloadUrl(urlString)
            val result = parser.parse(stream)

            return result
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



        override fun onPostExecute(result: List<Song>) {
            super.onPostExecute(result)


        }

}