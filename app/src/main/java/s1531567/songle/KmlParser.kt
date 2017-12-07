package s1531567.songle

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * Created by holly on 05/12/17.
 */

class KmlParser {

    private val ns: String? = null
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input : InputStream): List<Placemark>{
        input.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    false)
            parser.setInput(input, null)
            parser.nextTag()
            parser.nextTag()
            return readPlacemarks(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPlacemarks(parser: XmlPullParser): List<Placemark>
    {
        val entries = ArrayList<Placemark>()
        parser.require(XmlPullParser.START_TAG, ns, "Document")
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
        //Log.v("entries", entries.toString())
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPlacemark(parser: XmlPullParser): Placemark
    {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark")
        var name = ""
        var description = ""
        var styleURL = ""
        var point = mutableListOf<Double>()


        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue
            when(parser.name)
            {
                "name" -> name = readName(parser)
                "description" -> description = readDesc(parser)
                "styleUrl" -> styleURL = readStyle(parser)
                "Point" -> point =  readLatLong(parser)
                else ->  skip(parser)
            }
        }
        //Log.v("Placemark", Placemark(name, description, styleURL, point[0].toDouble(), point[1].toDouble()).toString())
        return Placemark(name, description, styleURL, point[0].toDouble(), point[1].toDouble())
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readName(parser: XmlPullParser): String
    {
        parser.require(XmlPullParser.START_TAG, ns, "name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "name")
        //Log.v("name", name)
        return name
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDesc(parser: XmlPullParser): String
    {
        parser.require(XmlPullParser.START_TAG, ns, "description")
        val desc = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "description")
        //Log.v("desc", desc)
        return desc
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStyle(parser: XmlPullParser): String
    {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl")
        val style = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl")
        Log.v("style", style)
        Log.v("is style empty", (style != "").toString())
        return style
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLatLong(parser: XmlPullParser): MutableList<Double> {
        //Log.v("Am i getting here?", "??")
        var doubles = mutableListOf<Double>()
        parser.require(XmlPullParser.START_TAG, ns, "Point")
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, ns, "coordinates")
        val link = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "coordinates")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, ns, "Point")

        val float = link.split(",").map { it -> it.toDouble() }
        doubles.add(float[0])
        doubles.add(float[1])
        //doubles.add(float[2])
        //Log.v("doubles", doubles.toString())
        return doubles
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