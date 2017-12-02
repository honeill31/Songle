package s1531567.songle

import android.content.Context

/**
 * Created by holly on 02/12/17.
 */
//Class to store achievements
class Achievements(private val context: Context) {

    private val tag = "achievements"

    val achievements = listOf(Achievement(
            title = "Arthur's seat",
            description = "You earned this by walking the distance of up and down Arthur's seat",
            difficulty = "Easy",
            hint = "Try walking"


    ), Achievement(
            title = "500 miles",
            description = "You earned this by being the man",
            difficulty = "Hard",
            hint = "Surely you don't need a hint for this one!"

    ), Achievement(
            title = "Biggest Fan",
            description = "You earned this by playing the YouTube video for a song more than 5 times",
            difficulty = "Medium",
            hint = ""
    ), Achievement(
            title = "Number of the beast",
            description = "You earned this by collecting 666 placemarks!",
            difficulty = "Medium",
            hint = ""
    )
            )



}