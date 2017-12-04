package s1531567.songle


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        butt.setOnClickListener{
                toast("Proceeding to login page...")
                val login = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(login)



            }

    }
}
