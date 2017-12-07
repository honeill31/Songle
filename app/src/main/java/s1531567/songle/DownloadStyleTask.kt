package s1531567.songle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
/**
 * Created by holly on 05/12/17.
 */
class DownloadStyleTask(songNum: Int, mapNum: Int) : AsyncTask<String, Int, List<Style>>() {
    val mSongNum = songNum
    val mMapNum = mapNum
    var url = mutableListOf<String>()


    override fun doInBackground(vararg params: String): List<Style> {
        val u = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/${Helper().intToString(mSongNum)}/map$mMapNum.txt"
        Log.v("doInBG Value", u)
        return loadKMLFromNetwork(u)
    }

    private fun loadKMLFromNetwork(urlString: String): List<Style> {
        val parser = StyleParser()
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


    inner class StyleParser {

        private val ns: String? = null
        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(input : InputStream): List<Style>{
            input.use {
                val parser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                        false)
                parser.setInput(input, null)
                parser.nextTag()
                parser.nextTag()
                return readStyles(parser)
            }
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readStyles(parser: XmlPullParser): List<Style>
        {
            val entries = ArrayList<Style>()
            parser.require(XmlPullParser.START_TAG, ns, "Document")
            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.eventType != XmlPullParser.START_TAG)
                {
                    continue
                }
                // Starts by looking for the entry tag
                if (parser.name == "Style")
                {
                    entries.add(readStyle(parser))
                }
                else
                {
                    skip(parser)
                }
            }
            //Log.v("entries", entries.toString())
            return entries
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readStyle(parser: XmlPullParser): Style
        {
            parser.require(XmlPullParser.START_TAG, ns, "Style")
            var id = parser.getAttributeValue(null, "id") ?: "noop"
            var iconURL = ""

            while (parser.next() != XmlPullParser.END_TAG){
                //Log.v("Parser ns", parser.namespace ?: "")
                if (parser.eventType != XmlPullParser.START_TAG)
                    continue

                when(parser.name){
                    "IconStyle" -> iconURL = readUrl(parser)
                    else ->  skip(parser)
                }
            }
            //Log.v("Placemark", Placemark(name, description, styleURL, point[0].toDouble(), point[1].toDouble()).toString())
            val url = URL(iconURL)
            val btmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            return Style(id, btmp)
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readUrl(parser: XmlPullParser): String {
            //Log.v("is this even " ,"being activated")
            var scale = ""

            parser.require(XmlPullParser.START_TAG, ns, "IconStyle")
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, ns, "scale")
            scale = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "scale")
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, ns, "Icon")
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, ns, "href")
            val url = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "href")
            parser.nextTag()
            parser.require(XmlPullParser.END_TAG, ns, "Icon")
            parser.nextTag()
            parser.require(XmlPullParser.END_TAG, ns, "IconStyle")


            return url
        }


        @Throws(IOException::class, XmlPullParserException::class)
        private fun readText(parser: XmlPullParser): String
        {
            var result = ""
            if (parser.next() == XmlPullParser.TEXT){
                result = parser.text
                parser.nextTag()
            }
            return result
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun skip(parser: XmlPullParser)
        {
            if (parser.eventType != XmlPullParser.START_TAG)
            {
                throw IllegalStateException()
            }
            var depth = 1
            while (depth != 0)
            {
                when (parser.next())
                {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }


    }
}