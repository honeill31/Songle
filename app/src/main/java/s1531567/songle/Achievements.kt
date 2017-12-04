package s1531567.songle

import android.content.Context

/**
 * Created by holly on 02/12/17.
 */
//Class to store achievements
class Achievements(private val context: Context) {
    private val tag = "achievements"

    //mutable so more achievements can be added later
    var achievements = mutableListOf(Achievement(
            title = "Arthur's seat",
            description = "You earned this by walking the distance of up and down Arthur's seat",
            difficulty = "Easy",
            id = 0,
            stepRqmt = 2000,
            locked = true
    ), Achievement(
            title = "500 miles",
            description = "You earned this by being the man",
            difficulty = "Hard",
            id = 1,
            stepRqmt = 1056000,
            locked = true

    ), Achievement(
            title = "Biggest Fan",
            description = "You earned this by playing the YouTube video for a song more than 5 times",
            difficulty = "Medium",
            id = 2,
            stepRqmt = 10,
            locked = true
    ), Achievement(
            title = "Number of the beast",
            description = "You earned this by collecting 666 placemarks!",
            difficulty = "Medium",
            id = 3,
            stepRqmt = 30,
            locked = true
    )
            )

    fun stepListener (steps : Int) : Achievement? {
        val achieve = achievements.filter { it.stepRqmt >= steps }
        for (a in achieve){
            if (prefs.checkAchievement(a.id)){ //if locked
                if (steps == a.stepRqmt){ //if at step requirement
                    a.locked = false
                    prefs.setAchievementUnlocked(a.id)
                    return a
                }
            }
        }
        return null
    }

    //fun placemarkListener (collected : Int) {

    //}



}