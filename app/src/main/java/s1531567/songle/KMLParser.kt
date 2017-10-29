package s1531567.songle

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * Created by holly on 29/10/17.
 */
class KMLParser {
    private val ns: String? = null
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: InputStream): List<Placemark> {
        input.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    false)
            parser.setInput(input, null)
            parser.nextTag()
            return readPlacemarks(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPlacemarks(parser: XmlPullParser): List<Placemark> {
        val entries = mutableListOf<Placemark>()
        parser.require(XmlPullParser.START_TAG, ns, "kml")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            Log.v("Parser Name:", parser.name)
            // Starts by looking for the entry tag
            if (parser.name == "Placemark") {
                entries.add(readPlacemark(parser))
            } else {
                parser.next()
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPlacemark(parser: XmlPullParser): Placemark {
        Log.v("Parser name (PLACEMARK)", parser.name)
        Log.v("Parser position (pm", parser.positionDescription)
        parser.require(XmlPullParser.START_TAG, ns, "Placemark")
        var name = ""
        var description = ""
        var styleURL = ""
        var point = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue
            when (parser.name) {
                "name" -> name = readName(parser)
                "description" -> description = readDescription(parser)
                "styleURL" -> styleURL = readStyleURL(parser)
                "Point" -> point = readPoint(parser)
                else -> parser.nextTag()
            }
        }
        return Placemark(name, description, styleURL, point)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "name")
        return name
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "description")
        val desc = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "description")
        return desc
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStyleURL(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "styleURL")
        val url = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "styleURL")
        return url
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPoint(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "Point")
        val point = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "Point")
        return point
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

}