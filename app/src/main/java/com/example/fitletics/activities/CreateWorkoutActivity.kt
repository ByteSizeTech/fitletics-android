package com.example.fitletics.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.fitletics.R
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import kotlinx.android.synthetic.main.activity_create_workout.*

class CreateWorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)


        val tempList: ArrayList<Exercise> = ArrayList()
        tempList.add(Exercise("BLEH", "5x564"))
        tempList.add(Exercise("Sit BLEH", "8x"))
        tempList.add(Exercise("BLEH", "1000x"))


        val workouts = arrayOf(Workout("Number uno", tempList, "Easy", "378 mins"),
            Workout("Numero deaux", tempList, "Easy", "456 mins"),
            Workout("theesra", tempList, "Easy", "347 mins"),
            Workout("arbaaa", tempList, "Easy", "16 mins"))

        val stringss = arrayOf("Lmao", "lol", "lel")


        create_workout_spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,  workouts.map { it.name }
        )

    }
}