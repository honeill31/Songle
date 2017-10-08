package s1531567.songle

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    fun Context.toast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }


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
