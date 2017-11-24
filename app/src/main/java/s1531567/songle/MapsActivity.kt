package s1531567.songle

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
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
import kotlinx.android.synthetic.main.guess_dialog.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.alertDialogLayout
import java.lang.Math.abs


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted = false
    private lateinit var mLastLocation: Location
    val closeBy = mutableListOf<KmlPlacemark>()
    lateinit var layer: KmlLayer
    lateinit var mediaplayer: MediaPlayer
    var currentSong: Int = 11
    var currentMap: Int = 4
    private lateinit var txt: EditText
    private lateinit var songs : List<Song>
    val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
    val editor = pref.edit()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

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
        mMap = googleMap

        try {
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            println("Security Exception Thrown [onMapReady]")
        }
        mMap.uiSettings.isMyLocationButtonEnabled = true
        val layerTask = KMLLayertask(mMap, applicationContext).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/11/map4.kml")
        layer = layerTask.get()
        layer.addLayerToMap() //displaying the kml tags

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
            var userGuess = txt.text.toString()
            Log.v("User guess", userGuess)
            println("USEW GUESS!!!! $userGuess")
            println("current song: ${songs[currentSong-1].title}")
            if (userGuess.trim().toLowerCase() == songs[currentSong].title.trim().toLowerCase()){
                //add currency,
                toast("${userGuess} is the correct song!")
            }
            if (userGuess.toLowerCase() != songs[currentSong].title.toLowerCase()){
                var tries = pref.getInt("Tries11", 3)
                tries -= tries
                toast("$userGuess is not the correct song, $tries tries remaining.")
                editor.putInt("Tries11", tries)
            }

        }
        b.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
            toast("aww")
        }

        val dialog = b.create()
        return dialog



    }


    override fun onResume() {
        super.onResume()
        mediaplayer.start()

        fab.setOnClickListener {
            val dialog = displayGuess()
            dialog.show()

        }


    }

    override fun onStop() {
        super.onStop()
        mediaplayer.stop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
        val current = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val editor = current.edit()
        editor.putInt("Current Song", currentSong)
        editor.putInt("Current Map", currentMap)
        editor.apply()
        val cur = current.getInt("Current Song", 100)
        println("hmmhmhmhm: ${cur}")
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


    fun checkCloseBy(current: Location?) = runBlocking<Unit>() {
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
                            Log.v("boop! I am in here", markLoc.toString())
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
            val writeWord: String = "$currentSong$currentMap"
            val parser = LyricParser()
            println(writeWord)
            for (mark in closeBy) {

                val pmwords = mark.getProperty("name").split(":")
                val dl = DownloadLyricTask("01")
                dl.execute()
                val lyrics = dl.get()
                val word = parser.findLyric(1, 1, lyrics, mark)
                Log.v("Word?", word.toString())
                val toWrite = "$writeWord$pmwords[0]$pmwords[1]"
                editor.putString(toWrite, "Collected")//setting this word to collected
                editor.apply()
            }

            //val hmm = current.getInt(0101)
            println("test:")
        }

    }

    override fun onConnectionSuspended(flag: Int) {
        println(">>>> On Connection Suspended")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        print(">>>>> Connection Failed")
    }
}
