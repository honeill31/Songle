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
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_achievement.*
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.toast

class AchievementActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.menu_home -> {
                val home = Intent(this, DefaultPage::class.java)
                startActivity(home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_map -> {
                if (Helper().checkInternet(connectivityManager)) {
                    val hmm = Intent(this@AchievementActivity, MapsActivity::class.java)
                    startActivity(hmm)
                }
                else toast("Connect to the internet to view the Map.")
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_list -> {
                if (Helper().checkInternet(connectivityManager)) {
                    val hmm = Intent(this@AchievementActivity, SongList::class.java)
                    startActivity(hmm)
                }
                else toast("Connect to the internet to view the SongList.")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private var myAdapter : AchievementAdapter? = null
    private lateinit var achievementList : List<Achievement>
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initialise()
    }

    private fun initialise(){
        val achievements = Achievements(this).achievements
        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        myAdapter = AchievementAdapter(this, achievements){
        }
        val decor = DividerItemDecoration(this)
        achList.addItemDecoration(decor)
        achList.layoutManager = layoutManager
        achList.adapter = myAdapter

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

        private fun drawVertical(c: Canvas, parent: RecyclerView) {
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


