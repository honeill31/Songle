package s1531567.songle

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageEvents.Event.NONE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.toast
import org.jetbrains.anko.vibrator


class MapsActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener,
        StepListener {


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var mLastLocation: Location
    private var closeBy = mutableListOf<Placemark>()
    private var markers = mutableListOf<MarkerOptions>()
    private lateinit var layer: List<Placemark>
    private var currentSong = 0
    private var currentMap = 0
    private var currentUser = ""
    private var scoreMode = 0
    private var standardiser = 0
    private val loginNotification = NotificationCompat.Builder(this)
    private lateinit var collectPlayer: MediaPlayer
    private lateinit var songs: List<Song>
    private val detect = StepDetector()
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    companion object : DownloadCompleteListener {
        override fun downloadComplete(result: Any) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginNotification.setAutoCancel(true)
        setContentView(R.layout.activity_maps)
        currentUser = prefs.currentUser
        songs = DownloadXMLTask(MapsActivity.Companion).execute().get()

        // Check whether a new version of Songle exists on the game server.
        update()

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
        scoreMode = prefs.getScoreMode(currentSong)
        collectPlayer = MediaPlayer.create(this, R.raw.collect)


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
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            detect.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    /*This function increases the number of steps walked when it detects a step. */
    override fun step(timeNs: Long) {
        var steps = prefs.steps
        steps++
        prefs.steps = steps
        if (steps % 100 == 0 && steps != 0) {
            // For user feedback while walking.
            val v = vibrator
            val soundPref = prefs.sharedPrefs.getBoolean("sounds", true)
            if (soundPref){
                v.vibrate(100)
            }

            var points = prefs.points
            points++
            prefs.points = points
            Log.v("Points", prefs.points.toString())
            toast("You walked 100 more steps! Point added.")
        }
        val stepListener = Achievements(this).stepListener(steps)
        if (stepListener != null) {
            toast("Achievement ${stepListener.title} unlocked!")
        }

    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val total = prefs.getPlacemarkTotal(currentSong)

        // Setting the Score Standardiser for this Map.
        standardiser = 1 / total


        try {
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            Log.v("SE [onMapReady]", se.toString())
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        //println("Adding layer! current map $currentMap")
        val layerTask = DownloadKMLTask(currentSong, currentMap, MapsActivity.Companion).execute()
        layer = layerTask.get()
        val styles = DownloadStyleTask(currentSong, currentMap).execute().get()
        //Log.v("Pms", layer.toString())
        addMarkers(styles)

        // Adding the placemarks to the map.
        for (mark in markers){
            mMap.addMarker(mark)
        }

        /* Listens for a user click event, displays the word of the placemark if collected, otherwise
           just the line and number */
        mMap.setOnMarkerClickListener {
            it.showInfoWindow()
            val contains = it.title.contains(":")
            if (contains) {
                val collected = (prefs.collectedPrev(currentSong,
                        currentMap,
                        it.title.split(":")[0].toInt(),
                        it.title.split(":")[1].toInt()))
                if (!collected) {
                    purchaseLyricDialog(it.title).show()
                }
            }
            true

        }
    }

    /* This function gets the correct information about each individual placemark such as whether
     * it has been collected or not, the word associated with it and if it has been collected,
      * assigning it a special 'green' collected style. Due to this taking a while to do it runs in
      * a coroutine. */
    private fun addMarkers(styles : List<Style>) = runBlocking {
        val job = launch {
            var bitmap = BitmapFactory.decodeResource(resources, R.drawable.grn_stars)
            val new = Bitmap.createScaledBitmap(bitmap, 75, 75, true)

                for (mark in layer) {
                    val currentStyle = styles.firstOrNull { it.id == mark.description }
                    val collected = prefs.collectedPrev(currentSong,
                            currentMap,
                            mark.name.split(":")[0].toInt(),
                            mark.name.split(":")[1].toInt())
                    //Log.v("collected", collected.toString())
                    if (!collected) {
                        markers.add(MarkerOptions()
                                .position(LatLng(mark.Lat, mark.Long))
                                .title(mark.name)
                                .icon(BitmapDescriptorFactory.fromBitmap(currentStyle!!.icon))

                        )
                    }
                    if (collected) {
                        val lyrics = DownloadLyricTask(currentSong).execute().get()
                        val title = LyricParser().findLyric(lyrics, mark)
                        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.grn_stars)

                        markers.add(MarkerOptions()
                                .position(LatLng(mark.Lat, mark.Long))
                                .title(title.word)
                                .icon(BitmapDescriptorFactory.fromBitmap(new))

                        )
                    }
                }
        }
        job.join()
    }





/* Function to allow users to purchase a placemark in exchange for their in-game currency */

    private fun purchaseLyricDialog(lineWord: String): AlertDialog {
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Purchase this word?")
        val marker = layer.firstOrNull { it.name == lineWord }
        println(marker.toString())
        var cost = 0
        when (marker!!.styleURL){
            "#unclassified" -> cost = 1
            "#boring" -> cost = 5
            "#notboring" -> cost = 10
            "#interesting" -> cost = 50
            "#veryinteresting" -> cost = 100
        }

        b.setMessage("This will cost $cost points")
        val parser = LyricParser()
        val split = lineWord.split(":")
        val line = split[0].toInt()
        val w = split[1].toInt()
        val dl = DownloadLyricTask(currentSong)
        dl.execute()
        val lyrics = dl.get()
        val word = parser.findLyric(lyrics, marker)
        val collectedPrev = prefs.collectedPrev(currentSong, currentMap, line, w)


        b.setPositiveButton("Purchase"){_,_->
            if (!collectedPrev){
                var points = prefs.points
                if (points >=cost){
                    // Setting this word to collected
                    prefs.setCollected(currentSong, currentMap, line, w)
                    var collected = prefs.wordsCollected(currentSong, currentMap)
                    collected++
                    points -= cost
                    prefs.points = points
                    prefs.setWordsCollected(currentSong, currentMap, collected)

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

    /**  Function to check for updates from the game server (inf.ed.ac.uk).
          -If update boolean is true, the local Song List is updated with the correct song
           information such as the number of Placemarks per map, per Song etc.
          -If firstRun is true, it unlocks 5 random songs for the user to play.
    */
    private fun update() {
        val needsUpdate = prefs.update
        val firstRun = prefs.firstRun
        if (needsUpdate) {
            prefs.timeStamp = Helper().DownloadStampTask(MapsActivity.Companion).execute().get()
            for (i in 1..songs.size) {
                var songTotal = 0
                for (j in 1..5) {
                    //Log.v("Got this far", "i:$i, j:$j")
                    var mapTotal : Int
                    var layerTask = DownloadKMLTask(i, j, MapsActivity.Companion).execute()
                    val thisLayer = layerTask.get()
                    mapTotal = thisLayer.size
                    prefs.gameEditor.putInt("Song $i Map $j Placemarks", mapTotal)
                    prefs.gameEditor.apply()
                    songTotal += mapTotal
                }
                prefs.gameEditor.putInt("Song $i total Placemarks", songTotal)

            }
            //unlocking 5 random songs
            if (firstRun){
                for (i in 1..5) {
                    val r = Helper().rand(0, songs.size)
                    //setting first song to be the first random song
                    if (i == 1) {
                        currentSong = r
                        currentMap = 1
                        prefs.gameEditor.putInt("$currentUser Current Song", r)
                        prefs.gameEditor.putInt("$currentUser Current Map", 1)
                        prefs.gameEditor.apply()
                    }
                    prefs.gameEditor.putBoolean("$currentUser Song $r locked", false)
                    prefs.gameEditor.putBoolean("$currentUser Song $r Map 1 locked", false)
                    prefs.gameEditor.apply()
                }
                prefs.firstRun = false
            }
            Log.v("prefs update", prefs.update.toString())
            prefs.update = false

        }

    }



    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()

    }

    override fun onPause() {
        super.onPause()

        /* Sending the user a Notification if they navigate away from the app with how close
         * they are to a placemark. */

        if ((closeBy.size != 0)){
            loginNotification.setSmallIcon(R.drawable.ic_music_note_black_24dp)
            loginNotification.setTicker("Placemark Closeby!")
            loginNotification.setWhen(System.currentTimeMillis())
            loginNotification.setContentTitle("Placemark Closeby!")
            val soundPref = prefs.sharedPrefs.getBoolean("sounds", true)
            if (soundPref){
                loginNotification.setVibrate(longArrayOf(100,100,100))
            }
            val pm = Location("")
            pm.latitude = closeBy[0].Lat
            pm.longitude = closeBy[0].Long
            val distance = mLastLocation.distanceTo(pm)
            loginNotification.setContentText("The nearest Placemark is only $distance metres away!")
            val intent = Intent(this, this::class.java)
            val pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            loginNotification.setContentIntent(pendingIntent)

            val nm : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(1234, loginNotification.build())

        }
    }

/*  Function to interact with the user's guess and perform appropriate actions such as:
          -Add Songle currency when guess is correct
          -Decrease tries remaining on incorrect guess
          -Decrease score after 3 incorrect guesses.
*/

    private fun displayGuess() : AlertDialog {

        val b = AlertDialog.Builder(this)
        val inf = LayoutInflater.from(this)
        val view = inf.inflate(R.layout.guess_dialog, null)
        b.setView(view)

        val txt = view.findViewById<EditText>(R.id.user_guess) as EditText

        b.setPositiveButton("Submit") { dialog, _ ->

            dialog.dismiss()
            val guessed = prefs.songGuessed(currentSong)
            var userGuess = txt.text.toString()

            if (userGuess.trim().toLowerCase() == songs[currentSong-1].title.trim().toLowerCase() && !guessed ){
                var currentSongles = prefs.songles
                currentSongles++
                prefs.songles = currentSongles
                prefs.setSongGuessed(currentSong)
                prefs.changeScoreMode(currentSong)
                toast("$userGuess is the correct song!")
            }
            if (userGuess.trim().toLowerCase() != songs[currentSong-1].title.trim().toLowerCase()){
                var tries = prefs.getTries(currentSong)
                Log.v("Current tries", tries.toString())
                tries--
                if (tries >0) {
                    prefs.setTries(currentSong, tries)
                    toast("Incorrect song guess, $tries tries remaining.")
                }
                if (tries <=0){
                    //decrease score
                    toast("Incorrect song guess, Score decreasing")
                    var points = prefs.points
                    points--
                    prefs.points = points
                }

            }

        }
        b.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return b.create()
    }

    /*  Function to convert from one in-game currency to another.      */
    private fun convertCurrency(from: String, to: String) : AlertDialog {

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

        b.setPositiveButton("Convert") { _, _ ->

            val userAmount = txt.text.toString().toInt()
            when(from){
                "points" -> cost = userAmount * 1000
                "songles" -> cost = userAmount / 1000
            }

            val fromTotal = fromCurrency-cost
            val toTotal = userAmount + toCurrency

            if (fromTotal>0){
                prefs.gameEditor.putInt(from, fromTotal)
                prefs.gameEditor.putInt(to,toTotal)
                prefs.gameEditor.apply()
                toast("Conversion Successful")
            }
            if (fromTotal<=0){
                toast("You do not have enough $from to make this conversion!")
            }

        }
        b.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        return b.create()

    }




    override fun onResume() {
        super.onResume()
        loginNotification.setAutoCancel(true)
        val guessed = prefs.songGuessed(currentSong)
        if (guessed) currentSongTxt.text = "Current Song: ${songs[currentSong-1].title}"
        if (!guessed) currentSongTxt.text = "Current Song: $currentSong"
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        detect.registerListener(this)
        sensorManager.registerListener(this@MapsActivity, accel, SensorManager.SENSOR_DELAY_FASTEST)


        /* When the user taps the Floating Action Button they will be able
           to guess the name of the song.
         */
        fab.setOnClickListener {
            val dialog = displayGuess()
            dialog.show()

        }

        /* When the user holds in the Floating Action Button they will be able
           to change the current song.
         */
        fab.setOnLongClickListener {
            val menu = PopupMenu(this@MapsActivity, findViewById(R.id.fab))
            for (i in songs.indices) {
                val locked = prefs.songLocked(i+1)
                val guessed = prefs.songGuessed(i+1)

                if (!locked && guessed){
                    menu.menu.add(NONE,i+1,NONE,"${i+1}: ${songs[i].title}")
                }
                if (!locked && !guessed){
                    menu.menu.add(NONE,i+1,NONE,"${i+1}: ???")
                }



            }
            /* Handling the change of Song */
            menu.setOnMenuItemClickListener {
                Log.v("Song Before", currentSong.toString())
                currentSong = it.itemId
                prefs.currentSong = currentSong
                prefs.currentMap = 1 //prevents opening unlocked maps
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
        collectPlayer.stop()
        if (Helper().checkInternet(connectivityManager)) {
            prefs.currentSong = currentSong
            prefs.currentMap = currentMap
            prefs.currentUser = currentUser
        }
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    private fun createLocationRequest() {
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
            createLocationRequest()
        } catch (ise: IllegalStateException) {
            println("IllegalStateException thrown [onConnected]")
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val lastLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            if (lastLoc == null){
                mLastLocation = Location("")
                mLastLocation.longitude = -3.187
                mLastLocation.latitude = 55.9445
            }
            if (lastLoc != null){
                mLastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            }

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /* This function checks how close the user is to each placemark, and if they are close enough
     * to collect the placemark, it adds them to the 'closeBy' list */
    private fun checkCloseBy(current: Location?)  = runBlocking {
        val job = launch {

            if (current == null) {
                println("[onLocationChanged] Location unknown")
            } else {
                /* As location has changed, we want to remove previous placemarks. */
                closeBy.clear()
                val mLoc = current
                for (mark in layer) {
                    var markLoc = Location("")
                    markLoc.longitude = mark.Long
                    markLoc.latitude = mark.Lat
                    if (mLoc.distanceTo(markLoc)<25){
                        closeBy.add(mark)
                    }
                }
            }
        }
        job.join()
    }

    /*  In onLocationChanged the following actions are performed:
        -Calls the function 'checkCloseBy' to get a list of nearby Placemarks.
        -When the 'Collect' Button is pressed:
              -Checks whether a placemark has been previously collected.
              -Finds the word associated with the placemark.
              -Adds the placemark to the list of collected placemarks for this map and song.
              -Adjusts user score accordingly.
     */
    override fun onLocationChanged(current: Location?) {
        checkCloseBy(current)
        collect.setOnClickListener {
            val parser = LyricParser()

            for (mark in closeBy) {
                var collected = prefs.wordsCollected(currentSong, currentMap)
                val currentScore1 = scoreModify1(collected)
                val currentScore2 = scoreModify2(collected)
                val total = currentScore1.toInt()+currentScore2.toInt()

                if (scoreMode == 1){
                    prefs.score1Total(currentSong, currentScore1.toInt())


                }
                if (scoreMode == 2){
                    prefs.score2Total(currentSong, currentScore2.toInt())
                }

                prefs.setSongScore(currentSong, total)
                var userScore = prefs.totalScore
                userScore += total
                prefs.totalScore = userScore

                val lineWord = mark.name.split(":")
                val line = lineWord[0].toInt()
                val w = lineWord[1].toInt()
                val dl = DownloadLyricTask(currentSong)
                dl.execute()
                val lyrics = dl.get()
                val word = parser.findLyric(lyrics, mark)
                Log.v("Word?", word.toString())
                val collectedPrev = prefs.collectedPrev(currentSong, currentMap, line, w)
                if (!collectedPrev){
                    prefs.setCollected(currentSong, currentMap, line, w)
                    collected++
                    prefs.setWordsCollected(currentSong, currentMap, collected)

                    val soundPref = prefs.sharedPrefs.getBoolean("sounds", true)
                    if (soundPref){
                        collectPlayer.start()
                    }
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
