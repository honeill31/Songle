package s1531567.songle

import java.util.*

/**
 * Created by holly on 04/12/17.
 */
class Helper {

    fun intToString(num : Int) : String {
        var str = ""
        if (num <=9){
            str = "0$num"
        }
        else {
            str = "$num"
        }
        return str

    }

    fun rand(from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
    }

    fun stringToInt(num : String) : Int {
        return num.toInt()

    }
}