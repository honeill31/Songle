package s1531567.songle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu_home -> {
                startActivity(Intent(this@UserActivity, DefaultPage::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_map -> {
                startActivity(Intent(this@UserActivity, MapsActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_list -> {
                startActivity(Intent(this@UserActivity, SongList::class.java))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val pref = getSharedPreferences(getString(R.string.PREFS_FILE), Context.MODE_PRIVATE)
        val userScore = pref.getInt("total score", 0).toString()
        score.text = "Current Score: $userScore"
        bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        achievement.setOnClickListener {
            startActivity(Intent(this@UserActivity, AchievementActivity::class.java))
        }
    }
}
