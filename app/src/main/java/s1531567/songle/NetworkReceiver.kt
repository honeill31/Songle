package s1531567.songle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.preference.PreferenceManager

/**
 * Created by s1531567 on 17/10/17.
 */
class NetworkReceiver () : BroadcastReceiver() {


    override fun onReceive(p0: Context?, p1: Intent?) {


        val networkPref = prefs.sharedPrefs.getString("Data", "Wi-Fi")

        val connMgr =
                p0?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val networkInfo = connMgr.activeNetworkInfo

        if (networkPref == "Wi-Fi" && networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
            //use wifi
        } else if (networkPref == "Data" && networkInfo != null){
            //have data permission
        } else {
            //return msg
        }


    }

}