package s1531567.songle

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.app.NavUtils
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_song_list.*
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.jetbrains.anko.toolbar
import android.app.AlertDialog

class ReviewActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu_home -> {
                val home = Intent(this@ReviewActivity, DefaultPage::class.java)
                startActivity(home)
            }
            R.id.menu_map -> {
                val hmm = Intent(this@ReviewActivity, MapsActivity::class.java)
                startActivity(hmm)


            }
            R.id.menu_list -> {
                val hmm = Intent(this@ReviewActivity, SongList::class.java)
                startActivity(hmm)
            }
        }
        true
    }

    private var myAdapter : MapAdapter? = null
    private var mapList = mutableListOf<MapInfo>()
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun onBackPressed() {
        super.onBackPressed()
        val back = Intent(this@ReviewActivity, SongList::class.java)
        startActivity(back)
    }

    private fun initialise(song: Int){

        val task = DownloadXMLTask()
        task.execute()

        for (i in 1..5){
            val totalPlacemark = prefs.getMapPlacemarkTotal(song, i)
            Log.v("totalplacemark", totalPlacemark.toString())
            val collectedPlacemark = prefs.getMapCollected(song, i)
            val locked = prefs.mapLocked(song, i)
            mapList.add(MapInfo(totalPlacemark,collectedPlacemark,song,i,locked))
        }

        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        myAdapter = MapAdapter(mapList){
            if (it.locked) {
                unlockMapDialog(it.mapNumber, it.songNumber).show()
            }
            if (!it.locked){
                toast("Activating Map ${it.mapNumber}")
                prefs.currentMap = it.mapNumber
                startActivity(Intent(this@ReviewActivity, MapsActivity::class.java))


            }

        }

        val decor = DividerItemDecoration(this)
        maplist.addItemDecoration(decor)
        maplist.layoutManager = layoutManager
        maplist.adapter = myAdapter

    }

    fun unlockMapDialog(mapNum: Int, songNum: Int) : AlertDialog {
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Unlock Map $mapNum?")
        b.setMessage("If you unlock before guessing, your score will change.")
        b.setPositiveButton("Unlock") { dialog, whichButton ->
            var collected = 0
            var total = 0
            var i = 1
            while (i < mapNum){
                collected += prefs.getMapCollected(songNum, i)
                total += prefs.getMapPlacemarkTotal(songNum, i)
                i++
            }
            if ((collected/total)<(0.66)){
                Log.v("Collectedfalse:", collected.toString())
                Log.v("total false :", total.toString())
                toast("Error, you have not collected enough placemarks to unlock the next map")
            }

            if ((collected/total)>=(0.66)){
                Log.v("Collected true:", "$collected")
                Log.v("total true:", "$total")
                Log.v("ratio true:", "${(collected/total)}")
                Log.v("2/3 ???? ", "${2/3}")
                prefs.setMapUnlocked(songNum, mapNum)
                prefs.currentMap = mapNum
                toast("Map unlocked!")
            }




        }
        b.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        return b.create()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        val extras = intent.extras
        val title = extras.getString("title")
        val artist = extras.getString("artist")
        val song = extras.getString("num").toInt()
        val url = extras.getString("url")
        review_bar.selectedItemId = R.id.menu_list
        initialise(song)

        review_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val guessed = prefs.songGuessed(song)
        if (guessed){
            play_giveup.text = getString(R.string.playSong)
            review_title.text = title
            review_artist.text = artist
        }
        if (!guessed){
            play_giveup.text = getString(R.string.giveUp)
            review_title.text = getString(R.string.questionMarks)
            review_artist.text = getString(R.string.questionMarks)
        }

        play_giveup.setOnClickListener{


            if (!guessed){
                prefs.changeScoreMode(song)
                toast("You Have given up! Changing score mode.")
            }
            if (guessed){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }

        }

        lyrics.setOnClickListener {
            val lyric = Intent(this@ReviewActivity, LyricsActivity::class.java)
            lyric.putExtra("Song number", song)
            startActivity(lyric)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val song = Intent(applicationContext, SongList::class.java)
        startActivityForResult(song, 0)
        return super.onOptionsItemSelected(item)

    }


    inner class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration(){
        private var divider: Drawable? = null

        init {
            val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
            divider = a.getDrawable(0)
            a.recycle()
        }

        override fun onDraw(c: Canvas?, parent: RecyclerView?) {
            drawVertical(c!!, parent!!)
        }

        fun drawVertical(c: Canvas, parent: RecyclerView) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0..childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider!!.intrinsicHeight
                divider?.setBounds(left, top, right, bottom)
                divider?.draw(c)
            }
        }


    }
}
