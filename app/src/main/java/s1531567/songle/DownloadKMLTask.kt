package s1531567.songle

import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by holly on 24/10/17.
 */
/* Class for Downloading Placemarks */
class DownloadKMLTask(songNum : Int, mapNum : Int, private val caller: DownloadCompleteListener) : AsyncTask<String,Int,List<Placemark>>() {
    private val mSongNum = songNum
    private val mMapNum = mapNum

    override fun doInBackground(vararg params: String): List<Placemark> {
        val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/${Helper().intToString(mSongNum)}/map$mMapNum.txt"
        //Log.v("doInBG Value", u)
        var pms = loadKMLFromNetwork(u)
/*        for (mark in pms){
            val lyrics = DownloadLyricTask(mSongNum).execute().get()
            val word = LyricParser().findLyric(lyrics, mark)
            val line = mark.name.split(":")[0].toInt()
            val w = mark.name.split(":")[1].toInt()
            val collectedPrev = prefs.collectedPrev(mSongNum, mMapNum, line, w)
            if (collectedPrev){
                mark.word = word.word
            }
            else mark.word = mark.name
        }*/
        return loadKMLFromNetwork(u)
    }

    private fun loadKMLFromNetwork(urlString: String): List<Placemark> {
        val parser = KmlParser()
        val stream = downloadUrl(urlString)
        //Log.v("stream", stream.toString())
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
        caller.downloadComplete(result)
    }
}