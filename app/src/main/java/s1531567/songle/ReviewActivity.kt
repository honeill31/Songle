package s1531567.songle

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_review.*
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NavUtils
import android.view.MenuItem
import android.widget.Toolbar
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.startActivityForResult
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

    override fun onBackPressed() {
        super.onBackPressed()
        val back = Intent(this@ReviewActivity, SongList::class.java)
        startActivity(back)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val extras = intent.extras
        val song = extras.getString("Song")
        val url = extras.getString("url")
        review_title.text = song

        review_bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        playsong.setOnClickListener{startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))}
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val song = Intent(applicationContext, SongList::class.java)
        startActivityForResult(song, 0)
        return super.onOptionsItemSelected(item)

    }
}
