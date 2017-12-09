package s1531567.songle


import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_user.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.FileNotFoundException
import java.io.IOException


class UserActivity : AppCompatActivity() {

    private val GET_IMAGE = 1


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

        // Displaying the correct User Score and User Name.
        val userScore = prefs.totalScore
        if (prefs.sharedPrefs.getString("username", "") != ""){
            user_profile_name.text = prefs.sharedPrefs.getString("username", "")
        }
        else  user_profile_name.text = prefs.currentUser.takeWhile { it != '@' }
        score.text = "Current Score: $userScore"
        bar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        achievement.setOnClickListener {
            startActivity(Intent(this@UserActivity, AchievementActivity::class.java))
        }

        logout.setOnClickListener {
            startActivity(Intent(this@UserActivity, LoginActivity::class.java))
            prefs.currentUser = ""
            prefs.loggedIn = false
        }

        user_profile_photo.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_IMAGE)

        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        delete.setOnClickListener {
            alert("Are you sure you want to delete your account?") {
                yesButton {
                    val userAccount = prefs.currentUser
                    val users = prefs.users.split(",")
                    val usersBefore = users.takeWhile { it.split(":")[0] != userAccount }
                    val usersAfter = users.dropWhile { it.split(":")[0] != userAccount }
                    prefs.users = "$usersBefore$usersAfter"
                    toast("Goodbye...")
                }
                noButton { }

            }.show()
        }

    }

    // This function displays the user's preferred image as their profile photo.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GET_IMAGE && resultCode == Activity.RESULT_OK){
            val selectedImg : Uri = data!!.data
            var bitmap : Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImg)
                val profileView = findViewById<ImageView>(R.id.user_profile_photo)
                profileView.setImageBitmap(bitmap)
            } catch (e : FileNotFoundException){
                e.printStackTrace()
            } catch (e : IOException){
                e.printStackTrace()
            }
        }
    }





}
