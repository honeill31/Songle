package s1531567.songle

import android.content.Context

/**
 * Created by holly on 02/12/17.
 */
/* Class to store achievements */
class Achievements(private val context: Context) {
    private val tag = "achievements"

    //mutable so more achievements can be added later
    var achievements = mutableListOf(Achievement(
            title = "Arthur's seat",
            description = "You earned this by walking the distance of up and down Arthur's seat",
            difficulty = "Easy",
            id = 0,
            pmRqmt = 10000000,
            stepRqmt = 5000,
            locked = true
    ), Achievement(
            title = "500 miles",
            description = "You earned this by being the man (or woman!) who walked 500 miles!",
            difficulty = "Hard",
            id = 1,
            stepRqmt = 1056000,
            pmRqmt = 10000000,
            locked = true
    ), Achievement(
            title = "Number of the beast",
            description = "You earned this by collecting 666 Placemarks!",
            difficulty = "Medium",
            id = 3,
            stepRqmt = 666,
            pmRqmt = 10000000,
            locked = true
    ), Achievement(
            title = "A 'Note-able' Achievement",
            description = "Collect 100 Placemarks",
            stepRqmt = 10000000,
            id = 4,
            pmRqmt = 100,
            locked = true,
            difficulty = "Medium"
    ), Achievement(
            title = "Completionist",
            description = "Unlock ",
            stepRqmt = 10000000,
            id = 5,
            pmRqmt = 10000000,
            locked = true,
            difficulty = "easy"
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

    fun placemarkListener (collected : Int) : Achievement? {
        val achieve = achievements.filter { it.pmRqmt >= collected }
        for (a in achieve){
            if (prefs.checkAchievement(a.id)){
                if (collected == a.pmRqmt){
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