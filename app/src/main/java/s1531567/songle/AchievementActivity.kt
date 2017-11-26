package s1531567.songle

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_achievement.*

class AchievementActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.menu_home -> {
                val home = Intent(this, DefaultPage::class.java)
                startActivity(home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_map -> {
                val map = Intent(this, MapsActivity::class.java)
                startActivity(map)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_list -> {
                val list = Intent(this, SongList::class.java)
                startActivity(list)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
