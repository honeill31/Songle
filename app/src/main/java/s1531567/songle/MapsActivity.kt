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
    private val closeBy = mutableListOf<KmlPlacemark>()
    private lateinit var layer: KmlLayer
    lateinit var mediaplayer: MediaPlayer
    var currentSong = 0
    var currentMap = 0
    private lateinit var songs: List<Song>
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val detect = StepDetector()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = pref.edit()

        currentSong = pref.getInt("Current Song", 1)
        currentMap = pref.getInt("Current Map", 1)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
            Log.v("SE [onMapReady]", se.toString())
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        println("Adding layer! current map $currentMap")
        val layerTask = KMLLayertask(mMap, applicationContext).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$song/map$currentMap.kml")
        layer = layerTask.get()
        layer.addLayerToMap() //displaying the kml tags



    }


    //random number generator
    fun rand(from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
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
            //unlocking 5 random songs
            for (i in 1..5){
                val r = rand(0, songs.size)
                //setting first song to be the first random song
                if (i==1){
                    currentSong = r
                    currentMap = 1
                    editor.putInt("Current Song", r)
                    editor.putInt("Current Map", 1)
                    editor.apply()
                }
                editor.putBoolean("Song $r locked", false)
                editor.putBoolean("Song $r Map 1 locked", false)
                editor.apply()

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
            val guessed = pref.getBoolean("Song $currentSong guessed", false)
            var userGuess = txt.text.toString()
            Log.v("User guess", userGuess)
            println("USEW GUESS!!!! $userGuess")
            println("current song: ${songs[currentSong-1].title}")
            if (userGuess.trim().toLowerCase() == songs[currentSong-1].title.trim().toLowerCase() && !guessed ){
                var currentSongles = pref.getInt("songles", 0)
                currentSongles++
                editor.putInt("songles", currentSongles)
                editor.putBoolean("Song $currentSong guessed", true)
                editor.apply()

                toast("${userGuess} is the correct song!")
            }
            if (userGuess.trim().toLowerCase() != songs[currentSong-1].title.trim().toLowerCase()){
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
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        currentMap = pref.getInt("Current Map", 1)
        val editor = pref.edit()
        val guessed = pref.getBoolean("Song ${currentSong} guessed", false)
        if (guessed){
            currentSongTxt.text = "Current Song: ${songs[currentSong-1].title}"
        }
        if (!guessed) {
            currentSongTxt.text = "Current Song: ${currentSong}"
        }







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
                val locked = pref.getBoolean("Song ${i+1} locked", true)
                val guessed = pref.getBoolean("Song ${i+1} guessed", false)

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
                editor.putInt("Current Song", currentSong)
                editor.apply()
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
        val current = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = current.edit()
        editor.putInt("Current Song", currentSong)
        editor.putInt("Current Map", currentMap)
        editor.apply()
        val cur = current.getInt("Current Song", 1)
        val map = current.getInt("Current Map", 1)
        println("Current SOng: ${cur}")
        println("Current Map: ${map}")

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
                    if (abs(markLoc.latitude - mLoc.latitude) < 0.05 && abs(markLoc.longitude - mLoc.longitude) < 0.05) {
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
            val parser = LyricParser()

            for (mark in closeBy) {

                val lineWord = mark.getProperty("name").split(":")
                val line = lineWord[0].toInt()
                val w = lineWord[1].toInt()
                val dl = DownloadLyricTask(currentSong)
                dl.execute()
                val lyrics = dl.get()
                val word = parser.findLyric(currentSong, currentMap, lyrics, mark)
                Log.v("Word?", word.toString())
                val collectedPrev = pref.getBoolean("$currentSong $currentMap $line $w", false)
                if (!collectedPrev){
                    editor.putBoolean("$currentSong $currentMap $line $w", true)//setting this word to collected
                    Log.v("actual values", "$currentSong $currentMap $line $w")
                    editor.apply()
                    var collected = pref.getInt("$currentSong $currentMap words collected", 0)
                    collected++
                    editor.putInt("$currentSong $currentMap words collected", collected)
                    editor.apply()
                    val didThisEvenWork = pref.getBoolean("$currentSong $currentMap $line $w", false)
                    println("Did this even work? $didThisEvenWork")
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
