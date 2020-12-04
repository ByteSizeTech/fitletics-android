package com.example.fitletics.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.support.Workout
import kotlinx.android.synthetic.main.activity_start_custom_workout.*
import kotlinx.android.synthetic.main.activity_start_standard_workout.*

class StartStandardWorkoutActivity : AppCompatActivity() {

    private var workoutObject: Workout? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    val TAG = "START_WORKOUT_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_standard_workout)

        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        begin_workout_button_standard_workout.isEnabled = false

        setupWorkout()
        setWorkoutTexts()
        populateWorkoutListView()

        begin_workout_button.setOnClickListener {
            startActiveSessionActivity()
        }

        startSession()

    }


    private fun setupWorkout() {
    }

    private fun setWorkoutTexts() {
    }

    private fun populateWorkoutListView() {
    }

    private fun startSession() {
    }

    private fun startActiveSessionActivity() {
    }
}