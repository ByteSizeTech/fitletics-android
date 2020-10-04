package com.example.fitletics.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.Exercise

class SharedWorkoutActivity : AppCompatActivity() {

    private var arrayList: ArrayList<Exercise>? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_workout)

        arrayList = intent.getSerializableExtra("Workout_ex_list") as ArrayList<Exercise>
        Log.d("AL_TEST", "AL: ${arrayList!!.size}")

        val intent = intent
        setupArrayList()
        setupActivityText(
            intent.getStringExtra("Workout_name")!!,
            intent.getStringExtra("Workout_time")!!
        )

        listView = findViewById(R.id.shared_workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, arrayList!!)
        listView?.adapter = listAdapter

        listView?.setOnItemClickListener{parent, view, position, id ->
            //TODO: Finish this
        }

    }

    private fun setupActivityText(name: String, time: String) {
        Log.d("WORKOUT_INFO", "name: ${name}, time: ${time}")

        val nameText: TextView = findViewById(R.id.shared_workout_title_text)
        val timeText: TextView = findViewById(R.id.shared_workout_time_text)

        nameText.text = name
        timeText.text = time
    }

    private fun setupArrayList() {
        if (arrayList?.isEmpty()!!){
            arrayList = ArrayList()
            arrayList?.add(Exercise("Crunches", "5x"))
            arrayList?.add(Exercise("Sit ups", "8x"))
            arrayList?.add(Exercise("Strtches", "10x"))
            arrayList?.add(Exercise("Squats", "3x"))
            arrayList?.add(Exercise("Pullups", "7x"))
        }
        else
            return
    }
}