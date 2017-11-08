package s1531567.songle


import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_song_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import s1531567.songle.R.string.current_song
import s1531567.songle.R.string.menu_list

class SongList : AppCompatActivity() {


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu_home -> {
                val home = Intent(this@SongList, DefaultPage::class.java)
                startActivity(home)
            }
            R.id.menu_map -> {
                val hmm = Intent(this@SongList, MapsActivity::class.java)
                startActivity(hmm)


            }
            R.id.menu_list -> {


            }
        }
        true
    }

    private var myAdapter : SongAdapter? = null
    private lateinit var songList : List<Song>
    private var layoutManager : RecyclerView.LayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)
        list_bar.selectedItemId = R.id.menu_list
        list_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initialise()

        }

    private fun initialise(){
        val task = DownloadXMLTask()
        task.execute()
        songList = task.get()
        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        myAdapter = SongAdapter(songList){
            toast("${it.title} selected!")
            val activ = Intent(this@SongList, ReviewActivity::class.java)
            activ.putExtra("Song", it.title)
            activ.putExtra("url", it.url)
            startActivity(activ)

        }
        val decor = DividerItemDecoration(this)
        songlist.addItemDecoration(decor)
        songlist.layoutManager = layoutManager
        songlist.adapter = myAdapter

    }
    override fun onResume() {
        super.onResume()


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



