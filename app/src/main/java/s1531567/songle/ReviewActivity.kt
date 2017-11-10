package s1531567.songle

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.app.NavUtils
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_song_list.*
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.jetbrains.anko.toolbar

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

    private fun initialise(){
        val task = DownloadXMLTask()
        task.execute()

        mapList.add(MapInfo(30,20,1, false))
        mapList.add(MapInfo(46,4,2, false))
        mapList.add(MapInfo(620,603,3, true))
        mapList.add(MapInfo(173,20,4, true))
        mapList.add(MapInfo(200,20,5, true))
        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        myAdapter = MapAdapter(mapList){
            toast("${it.collectedPlacemark} Collected!")


        }

        val decor = DividerItemDecoration(this)
        maplist.addItemDecoration(decor)
        maplist.layoutManager = layoutManager
        maplist.adapter = myAdapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        val extras = intent.extras
        val title = extras.getString("title")
        val artist = extras.getString("artist")
        val url = extras.getString("url")
        review_bar.selectedItemId = R.id.menu_list
        review_title.text = "???"
        review_artist.text = "???"
        initialise()

        review_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        playsong.setOnClickListener{startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))}
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
