package s1531567.songle

import android.location.Location
import android.util.Xml
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.data.Geometry
import com.google.maps.android.data.Point
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.IOException
import java.io.InputStream


/**
 * Created by s1531567 on 28/10/17.
 */
class KMLHandler {

    fun closeBy(stream: InputStream, location: Location) : List<Placemark> {
        val result = mutableListOf<Placemark>()
        val parser = KMLParser()
        val placemarks = parser.parse(stream)

        for (mark in placemarks){
            val points = mark.point.split(",")
            if (points[0].toDouble()-location.latitude <0.05 && points[1].toDouble()-location.longitude<0.05){
                result.add(mark)
            }
        }

        return result


    }




    inner class KMLParser {
        private val ns: String? = null
        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(input : InputStream): List<Placemark>{
            input.use {
                val parser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                        false)
                parser.setInput(input, null)
                parser.nextTag()
                return readSongs(parser)
            }
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readSongs(parser: XmlPullParser): List<Placemark>
        {
            val entries = ArrayList<Placemark>()
            parser.require(XmlPullParser.START_TAG, ns, "Placemark")
            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.eventType != XmlPullParser.START_TAG)
                {
                    continue
                }
                // Starts by looking for the entry tag
                if (parser.name == "Placemark")
                {
                    entries.add(readPlacemark(parser))
                }
                else
                {
                    skip(parser)
                }
            }
            return entries
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readPlacemark(parser: XmlPullParser): Placemark
        {
            parser.require(XmlPullParser.START_TAG, ns, "Placemark")
            var name = ""
            var description = ""
            var styleURL = ""
            var point = ""

            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.eventType != XmlPullParser.START_TAG)
                    continue
                when(parser.name)
                {
                    "name" -> name = readName(parser)
                    "descrption" -> description = readDescription(parser)
                    "styleURL" -> styleURL = readStyleURL(parser)
                    "Point" -> point = readPoint(parser)
                    else ->  skip(parser)
                }
            }
            return Placemark(name, description, styleURL, point )
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readName(parser: XmlPullParser): String
        {
            parser.require(XmlPullParser.START_TAG, ns, "name")
            val name = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "name")
            return name
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readDescription(parser: XmlPullParser): String
        {
            parser.require(XmlPullParser.START_TAG, ns, "description")
            val desc = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "decription")
            return desc
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readStyleURL(parser: XmlPullParser): String
        {
            parser.require(XmlPullParser.START_TAG, ns, "styleURL")
            val url = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "styleURL")
            return url
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readPoint(parser: XmlPullParser): String
        {
            parser.require(XmlPullParser.START_TAG, ns, "Point")
            val point = readText(parser)
            parser.require(XmlPullParser.END_TAG, ns, "Point")
            return point
        }



        @Throws(IOException::class, XmlPullParserException::class)
        private fun readText(parser: XmlPullParser): String
        {
            var result = ""
            if (parser.next() == XmlPullParser.TEXT)
            {
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