package s1531567.songle

import android.Manifest
import android.app.AlertDialog
import android.app.usage.UsageEvents.Event.NONE
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlin.collections.filter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.Feature
import com.google.maps.android.data.Layer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.*
import java.lang.Math.abs
import java.net.InetAddress
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
        StepListener {


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var mLastLocation: Location
    private var closeBy = mutableListOf<KmlPlacemark>()
    private lateinit var layer: KmlLayer
    lateinit var mediaplayer: MediaPlayer
    var currentSong = 0
    var currentMap = 0
    var scoreMode = 0
    var standardiser = 0
    private lateinit var songs: List<Song>
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val detect = StepDetector()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        currentSong = prefs.currentSong
        currentMap = prefs.currentMap
        scoreMode = prefs.sharedPrefs!!.getInt("Song $currentSong score mode", 1)




        bar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val home = Intent(this@MapsActivity, DefaultPage::class.java)
                    startActivity(home)
                }
                R.id.menu_list -> {
                    val list = Intent(this@MapsActivity, SongList::class.java)
                    startActivity(list)
                }
                R.id.menu_map -> {


                }

            }
            true


        }

        mediaplayer = MediaPlayer.create(this@MapsActivity, R.raw.song)
        val task = DownloadXMLTask()
        task.execute()
        songs = task.get()

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            detect.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    override fun step(timeNs: Long) {
        var steps = prefs.steps
        steps++
        prefs.steps = steps
        if (steps % 1000 == 0 && steps != 0) {
            //for user feedback while walking
            val v = vibrator
            v.vibrate(100)

            var points = prefs.sharedPrefs.getInt("points", 0)
            points++
            prefs.editor.putInt("points", points)
            prefs.editor.apply()
            toast("You walked 1000 more steps! Point added.")
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val song = intToString(currentSong)
        mMap = googleMap
        firstRun()
//        val total = pref.getInt("Song $currentSong Map $currentMap Placemarks", 0)
    //    standardiser = 1/total


        try {
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            Log.v("SE [onMapReady]", se.toString())
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        println("Adding layer! current map $currentMap")
        val layerTask = KMLLayertask(mMap, applicationContext).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$song/map$currentMap.kml")
        layer = layerTask.get()
        var id = ""
        layer.addLayerToMap() //displaying the kml tags
        layer.setOnFeatureClickListener (object: Layer.OnFeatureClickListener{

            override fun onFeatureClick(feature: Feature) {
                val lineWord = feature.getProperty("name").split(":")
                Log.v("Feature style", feature.id)
                purchaseLyricDialog(lineWord, feature as KmlPlacemark).show()
            }
        })
    }

    fun purchaseLyricDialog(lineWord: List<String>, mark : KmlPlacemark): AlertDialog {
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Purchase this word?")
        var cost = 0
        when (mark.styleId){
            "#unclassified" -> cost = 10
            "#boring" -> cost = 5
            "#notboring" -> cost = 10
            "#interesting" -> cost = 150
            "#veryinteresting" -> cost = 200

        }

        b.setMessage("This will cost $cost points")
        val parser = LyricParser()
        val line = lineWord[0].toInt()
        val w = lineWord[1].toInt()
        val dl = DownloadLyricTask(currentSong)
        dl.execute()
        val lyrics = dl.get()
        val word = parser.findLyric(currentSong, currentMap, lyrics, mark)
        val collectedPrev = prefs.collectedPrev(line, w)


        b.setPositiveButton("Purchase"){_,_->
            if (!collectedPrev){
                var points = prefs.points
                if (points >=cost){
                    prefs.setCollected(line, w)//setting this word to collected
                    var collected = prefs.wordsCollected()
                    collected++
                    points -= cost
                    prefs.points = points
                    prefs.setWordsCollected(collected)

                    toast("You collected the word '${word.word}'!")
                }
                toast("You don't have enough points to purchase this word!")
            }
            toast("You already have this word!")

        }

        b.setNegativeButton("Cancel") {_,_ ->

        }
        return b.create()



    }

    //random number generator
    fun rand(from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
    }


    private fun firstRun() {
        val firstRun = prefs.sharedPrefs.getBoolean("firstRun", true)
        if (firstRun) {
            val progress = findViewById<ProgressBar>(R.id.progressBar)
            progress.progress = 0
            val progressBarStep = 100/songs.size
            progress.visibility = View.VISIBLE
            for (i in 1..songs.size) {
                var songTotal = 0
                for (j in 1..5) {
                    Log.v("Got this far", "i:$i, j:$j")
                    var mapTotal = 0
                    var layerTask = KMLLayertask(mMap, applicationContext).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/${intToString(i)}/map$j.kml")
                    val thisLayer = layerTask.get()
                    thisLayer.addLayerToMap()
                    doAsync {
                        mapTotal = thisLayer.containers.iterator().next().placemarks.count()
                        prefs.editor.putInt("Song $i Map $j Placemarks", mapTotal)
                        prefs.editor.apply()
                        songTotal += mapTotal

                    }
                    thisLayer.removeLayerFromMap()


                }
                progress.progress += progressBarStep
                prefs.editor.putInt("Song $i total Placemarks", songTotal)

            }
            //unlocking 5 random songs
            for (i in 1..5){
                val r = rand(0, songs.size)
                //setting first song to be the first random song
                if (i==1){
                    currentSong = r
                    currentMap = 1
                    prefs.editor.putInt("Current Song", r)
                    prefs.editor.putInt("Current Map", 1)
                    prefs.editor.apply()
                }
                prefs.editor.putBoolean("Song $r locked", false)
                prefs.editor.putBoolean("Song $r Map 1 locked", false)
                prefs.editor.apply()

            }
            progress.visibility = View.INVISIBLE
            prefs.editor.putBoolean("firstRun", false)
            prefs.editor.apply()

        }

    }



    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()

    }

    fun displayGuess() : AlertDialog {

        val b = AlertDialog.Builder(this)
        val inf = LayoutInflater.from(this)
        val view = inf.inflate(R.layout.guess_dialog, null)
        b.setView(view)

        var txt = view.findViewById<EditText>(R.id.user_guess) as EditText

        b.setPositiveButton("Submit") { dialog, whichButton ->

            dialog.dismiss()
            val guessed = prefs.songGuessed()
            var userGuess = txt.text.toString()

            if (userGuess.trim().toLowerCase() == songs[currentSong-1].title.trim().toLowerCase() && !guessed ){
                var currentSongles = prefs.songles
                currentSongles++
                prefs.songles = currentSongles
                prefs.setSongGuessed()
                toast("${userGuess} is the correct song!")
            }
            if (userGuess.trim().toLowerCase() != songs[currentSong-1].title.trim().toLowerCase()){
                var tries = prefs.getTries()
                Log.v("Current tries", tries.toString())
                tries--
                if (tries >0) {
                    prefs.setTries(tries)
                    toast("Incorrect song guess, $tries tries remaining.")
                }
                if (tries <=0){
                    //decrease score
                    toast("Incorrect song guess, points decreasing")
                    var points = prefs.points
                    points--
                    prefs.points = points
                }

            }

        }
        b.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = b.create()
        return dialog



    }

    //Function to convert from one currency to another.
    fun convertCurrency(from: String, to: String) : AlertDialog {

        val b = AlertDialog.Builder(this)
        val inf = LayoutInflater.from(this)
        val view = inf.inflate(R.layout.convert_dialog, null)
        b.setView(view)

        var fromCurrency = 0
        var toCurrency = 0
        var cost = 0

        if (from == "points"){
            fromCurrency = prefs.points
            toCurrency = prefs.songles

        }
        if (from == "songles"){
            fromCurrency = prefs.songles
            toCurrency = prefs.points
        }

        val txt = view.findViewById<EditText>(R.id.convert) as EditText
        val before = view.findViewById<TextView>(R.id.currencyNow)
        val after = view.findViewById<TextView>(R.id.currencyAfter)
        before.text = "Current $from: $fromCurrency"
        after.text = "Current $to: $toCurrency"

        b.setPositiveButton("Convert") { dialog, whichButton ->

            dialog.dismiss()
            var userAmount = txt.text.toString().toInt()
            //assert(userAmount%10==0)

            if (from == "points") {
                cost = userAmount * 10
            }
            if (from == "songles") {
                cost = userAmount / 10
            }

            val fromTotal = fromCurrency-cost
            val toTotal = userAmount + toCurrency

            if (fromTotal>0){
                prefs.editor.putInt(from, fromTotal)
                prefs.editor.putInt(to,toTotal)
                prefs.editor.apply()
                toast("Conversion Successful")
            }
            if (fromTotal<=0){
                toast("You do not have enough $from to make this conversion!")
            }

        }
        b.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = b.create()
        return dialog

    }

    fun checkInternet(): Boolean {
        var connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED)

       return connected

    }

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


    override fun onResume() {
        super.onResume()
        val guessed = prefs.songGuessed()
        if (guessed) currentSongTxt.text = "Current Song: ${songs[currentSong-1].title}"
        if (!guessed) currentSongTxt.text = "Current Song: ${currentSong}"







        val connected = checkInternet()
        if (!connected){
            toast("Not connected to the internet!")
            println("Not connected")
            val intent = Intent(this, DefaultPage::class.java)
            startActivity(intent)
        }


        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        detect.registerListener(this)
        sensorManager.registerListener(this@MapsActivity, accel, SensorManager.SENSOR_DELAY_FASTEST)



        //mediaplayer.start()

        fab.setOnClickListener {
            val dialog = displayGuess()
            dialog.show()

        }

        fab.setOnLongClickListener {
            val menu = PopupMenu(this@MapsActivity, findViewById(R.id.fab))
            for (i in songs.indices) {
                val locked = prefs.sharedPrefs!!.getBoolean("Song ${i+1} locked", true)
                val guessed = prefs.sharedPrefs!!.getBoolean("Song ${i+1} guessed", false)

                if (!locked && guessed){
                    menu.menu.add(NONE,i+1,NONE,"${i+1}: ${songs[i].title}")
                }
                if (!locked && !guessed){
                    menu.menu.add(NONE,i+1,NONE,"${i+1}: ???")
                }



            }
            menu.setOnMenuItemClickListener {
                Log.v("Song Before", currentSong.toString())
                currentSong = it.itemId
                prefs.editor.putInt("Current Song", currentSong)
                prefs.editor.apply()
                Log.v("Song now:", currentSong.toString())
                menu.dismiss()
                val intent = Intent(this@MapsActivity, this@MapsActivity::class.java)
                startActivity(intent)
                true

            }
            menu.show()
            true

        }

        points.setOnClickListener{
            val dialog = convertCurrency("points", "songles")
            dialog.show()
        }

        songles.setOnClickListener {
            val dialog = convertCurrency("songles", "points")
            dialog.show()
        }



    }

    override fun onStop() {
        super.onStop()
       // mediaplayer.stop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
        prefs.currentSong = currentSong
        prefs.currentMap = currentMap



    }

    fun createLocationRequest() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this

            )
        }
    }

    override fun onConnected(connectionHint: Bundle?) {
        try {
            createLocationRequest();
        } catch (ise: IllegalStateException) {
            println("IllegalStateException thrown [onConnected]")
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    fun checkCloseBy(current: Location?) = runBlocking {
        val job = launch {


            if (current == null) {
                println("[onLocationChanged] Location unknown")
            } else {
                val mLoc = LatLng(current.latitude, current.longitude)
                for (mark in layer.containers.iterator().next().placemarks) {
                    var markLoc: LatLng = mark.geometry.geometryObject as LatLng
                    if (abs(markLoc.latitude - mLoc.latitude) < 0.0005 && abs(markLoc.longitude - mLoc.longitude) < 0.0005) {
                        if (mark !in closeBy) {
                            closeBy.add(mark)
                        }

                    }
                }
            }
        }
        job.join()
    }


    override fun onLocationChanged(current: Location?) {
        checkCloseBy(current)
        //parse kmlplacemarks to placemarks and add them to the collected number
        collect.setOnClickListener {
            val parser = LyricParser()

            /*iterates through every placemark and performs the following operations:
            -Calculates the score depending on the score mode.
            -Checks whether the placemark has been previously collected.
            -Finds the word associated with the placemark
            -Adds the placemark to the list of collected placemarks for this map and song.
            */

            for (mark in closeBy) {
                var collected = prefs.sharedPrefs!!.getInt("$currentSong $currentMap words collected", 0)
                val currentScore1 = scoreModify1(collected)
                val currentScore2 = scoreModify2(collected)
                val total = currentScore1.toInt()+currentScore2.toInt()

                if (scoreMode == 1){
                    prefs.editor.putInt("Song $currentSong score 1", currentScore1.toInt())
                    prefs.editor.apply()


                }
                if (scoreMode == 2){
                    prefs.editor.putInt("Song $currentSong score 2", currentScore2.toInt())
                    prefs.editor.apply()
                }

                prefs.editor.putInt("Song $currentSong total score", total)
                var userScore = prefs.sharedPrefs.getInt("total score", 0)
                userScore += total
                prefs.editor.putInt("total score", userScore)
                prefs.editor.apply()

                val lineWord = mark.getProperty("name").split(":")
                val line = lineWord[0].toInt()
                val w = lineWord[1].toInt()
                val dl = DownloadLyricTask(currentSong)
                dl.execute()
                val lyrics = dl.get()
                val word = parser.findLyric(currentSong, currentMap, lyrics, mark)
                Log.v("Word?", word.toString())
                val collectedPrev = prefs.sharedPrefs.getBoolean("$currentSong $currentMap $line $w", false)
                if (!collectedPrev){
                    prefs.editor.putBoolean("$currentSong $currentMap $line $w", true)//setting this word to collected
                    prefs.editor.apply()
                    collected++
                    prefs.editor.putInt("$currentSong $currentMap words collected", collected)
                    prefs.editor.apply()
                    toast("You collected the word '${word.word}'!")

                }

            }
        }

    }

    override fun onConnectionSuspended(flag: Int) {
        println(">>>> On Connection Suspended")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        print(">>>>> Connection Failed")
    }


    private fun scoreModify1 (collected: Int) : Number {
        var m = 0
        when(currentMap){
            1 -> m = 500
            2 -> m = 400
            3 -> m = 250
            4 -> m = 200
            5 -> m = 150
        }
        return m - (collected * standardiser * m)
    }

    private fun scoreModify2(collected : Int) : Number {
        return collected * standardiser * 500
    }
}
