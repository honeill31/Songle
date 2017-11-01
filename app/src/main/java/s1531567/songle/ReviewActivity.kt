package s1531567.songle

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.content.Intent
import android.net.Uri

class ReviewActivity : AppCompatActivity() {
    private var currentSong : Song? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val home = Intent(this@ReviewActivity, DefaultPage::class.java)
                startActivity(home)
            }
            R.id.navigation_dashboard -> {

            }
            R.id.navigation_notifications -> {
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        playsong.setOnClickListener{startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentSong?.url)))}
    }
}
