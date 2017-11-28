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
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.*
import java.lang.Math.abs
import java.net.InetAddress


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
        StepListener {


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted = false
    private lateinit var mLastLocation: Location
    val closeBy = mutableListOf<KmlPlacemark>()
    lateinit var layer: KmlLayer
    lateinit var mediaplayer: MediaPlayer
    var currentSong: Int = 1
    var currentMap: Int = 1
    private lateinit var songs: List<Song>
    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val detect = StepDetector()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        bar.selectedItemId = R.id.menu_map
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val current = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        currentSong = current.getInt("Current Song", 1)
        currentMap = current.getInt("Current Map", 1)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()



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
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        var steps = pref.getInt("steps", 0)
        steps++
        val editor = pref.edit()
        editor.putInt("steps", steps)
        editor.apply()
        toast("Steps: $steps")
        if (steps % 1000 == 0 && steps != 0) {
            //for user feedback while walking
            val v = vibrator
            v.vibrate(100)

            var points = pref.getInt("points", 0)
            points++
            editor.putInt("points", points)
            editor.apply()
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


        try {
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            println("Security Exception Thrown [onMapReady]")
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        val layerTask = KMLLayertask(mMap, applicationContext).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$song/map$currentMap.kml")
        layer = layerTask.get()
        layer.addLayerToMap() //displaying the kml tags



    }


    private fun firstRun() {
        val firstRun = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE).getBoolean("firstRun", true)
        if (firstRun) {
            val editor = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE).edit()
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
                        editor.putInt("Song $i Map $j Placemarks", mapTotal)
                        editor.apply()
                        songTotal += mapTotal

                    }
                    thisLayer.removeLayerFromMap()


                }
                progress.progress += progressBarStep
                editor.putInt("Song $i total Placemarks", songTotal)

            }
            progress.visibility = View.INVISIBLE
            editor.putBoolean("firstRun", false)
            editor.apply()

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
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()

        var txt = view.findViewById<EditText>(R.id.user_guess) as EditText

        b.setPositiveButton("Submit") { dialog, whichButton ->

            dialog.dismiss()
            var userGuess = txt.text.toString()
            Log.v("User guess", userGuess)
            println("USEW GUESS!!!! $userGuess")
            println("current song: ${songs[currentSong-1].title}")
            if (userGuess.trim().toLowerCase() == songs[currentSong].title.trim().toLowerCase()){
                var currentSongles = pref.getInt("songles", 0)
                currentSongles++
                editor.putInt("songles", currentSongles)
                editor.putBoolean("Song $currentSong guessed", true)
                editor.apply()

                toast("${userGuess} is the correct song!")
            }
            if (userGuess.toLowerCase() != songs[currentSong].title.toLowerCase()){
                var tries = pref.getInt("Tries$currentSong", 3)
                Log.v("Current tries", tries.toString())
                tries--
                if (tries >0) {
                    toast("Incorrect song guess, $tries tries remaining.")
                    editor.putInt("Tries$currentSong", tries)
                    editor.apply()
                }
                if (tries <=0){
                    //decrease score
                    toast("Incorrect song guess, points decreasing")
                    var points = pref.getInt("points", 0)
                    points--
                    editor.putInt("points", points)
                    editor.apply()
                }

            }

        }
        b.setNegativeButton("Cancel") { dialog, which ->
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
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()
        var fromCurrency = 0
        var toCurrency = 0
        var cost = 0

        if (from == "points"){
            fromCurrency = pref.getInt(from, 0)
            toCurrency = pref.getInt(to, 0)

        }
        if (from == "songles"){
            fromCurrency = pref.getInt(from, 0)
            toCurrency = pref.getInt(to, 0)
        }

        val txt = view.findViewById<EditText>(R.id.convert) as EditText
        val before = view.findViewById<TextView>(R.id.currencyNow)
        val after = view.findViewById<TextView>(R.id.currencyAfter)
        before.text = "Current $from: $fromCurrency"
        after.text = "Current $to: $toCurrency"

        b.setPositiveButton("Convert") { dialog, whichButton ->

            dialog.dismiss()
            var userAmount = txt.text.toString().toInt()

            if (from == "points") {
                cost = userAmount * 10
            }
            if (from == "songles") {
                cost = userAmount / 10
            }

            val fromTotal = fromCurrency-cost
            val toTotal = userAmount + toCurrency

            if (fromTotal>0){
                editor.putInt(from, fromTotal)
                editor.putInt(to,toTotal)
                editor.apply()
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
        try {
            val address = InetAddress.getByName("google.com")
            Log.v("Address:", address.toString())
            return !address.equals("")
        } catch (e: Exception) {
            return false
        }
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




        val connected = checkInternet()
        if (connected){
            //display error
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
                menu.menu.add(NONE,i+1,NONE,songs[i].title)
            }
            menu.setOnMenuItemClickListener {
                Log.v("Song Before", currentSong.toString())
                currentSong = it.itemId
                Log.v("Song now:", currentSong.toString())
                menu.dismiss()
                onStop()
                recreate()
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
        val current = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = current.edit()
        editor.putInt("Current Song", currentSong)
        editor.putInt("Current Map", currentMap)
        editor.apply()
        val cur = current.getInt("Current Song", 1)
        println("hmmhmhmhm: ${cur}")
        finish()
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
                val current = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
                for (mark in layer.containers.iterator().next().placemarks) {
                    val markLoc: LatLng = mark.geometry.geometryObject as LatLng
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
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()
        //parse kmlplacemarks to placemarks and add them to the collected number
        collect.setOnClickListener {
            val songMap = "$currentSong $currentMap"
            val parser = LyricParser()
            println(songMap)
            for (mark in closeBy) {

                val lineWord = mark.getProperty("name").split(":")
                val line = intToString(lineWord[0].toInt())
                val w = intToString(lineWord[1].toInt())
                val dl = DownloadLyricTask("${intToString(currentSong)}")
                dl.execute()
                val lyrics = dl.get()
                val word = parser.findLyric(currentSong, currentMap, lyrics, mark)
                Log.v("Word?", word.toString())
                val toWrite = "$songMap $line $w"
                val collectedPrev = pref.getBoolean(toWrite, false)
                if (!collectedPrev){
                    editor.putBoolean(toWrite, true)//setting this word to collected
                    var collected = pref.getInt("$songMap words collected", 0)
                    collected++
                    editor.putInt("$songMap words collected", collected)
                    editor.apply()
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
}
