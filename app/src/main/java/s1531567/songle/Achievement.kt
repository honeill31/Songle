package s1531567.songle

/**
 * Created by s1531567 on 26/11/17.
 */
data class Achievement(val title : String,
                       val description: String,
                       val difficulty : String,
                       val id: Int,
                       val stepRqmt : Int,
                       var locked : Boolean)