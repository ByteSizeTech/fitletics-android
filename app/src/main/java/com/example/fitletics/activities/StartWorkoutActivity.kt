package com.example.fitletics.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.Exercise

class StartWorkoutActivity : AppCompatActivity() {

    private var arrayList: ArrayList<Exercise>? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_workout)

//        var original: ArrayList<Exercise>? = null
//        original = ArrayList()
//        original?.add(Exercise("test1", "lol"))
//        original?.add(Exercise("test2", "30a"))
//        var copy: ArrayList<Exercise>? = null
//        Log.d("AL_TEST_TEST", "AL: ${original[0].name}")
//        copy = original
//        Log.d("AL_TEST_TEST", "AL: ${copy[0].name}")


        //arrayList = ArrayList()
        arrayList = intent.getSerializableExtra("Workout_ex_list") as ArrayList<Exercise>
        Log.d("AL_TEST", "AL: ${arrayList!!.size}")

        val intent = intent
        setupArrayList()
        setupActivityText(
            intent.getStringExtra("Workout_name")!!,
            intent.getStringExtra("Workout_time")!!,
            intent.getStringExtra("Workout_diff")!!
        )

        listView = findViewById(R.id.workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, arrayList!!)
        listView?.adapter = listAdapter


        listView?.setOnItemClickListener{parent, view, position, id ->
            //TODO: Finish this
        }
    }

    private fun setupActivityText(name: String, time: String, diff: String) {
        Log.d("WORKOUT_INFO", "name: ${name}, time: ${time}, diff: ${diff}")

        val nameText: TextView = findViewById(R.id.workout_title_text)
        val timeText: TextView = findViewById(R.id.workout_time_text)
        val diffText: TextView = findViewById(R.id.workout_difficulty_text)

        nameText.text = name
        timeText.text = time
        diffText.text = diff
    }

    private fun setupArrayList() {
        if (arrayList?.isEmpty()!!){
            arrayList = ArrayList()
            arrayList?.add(Exercise(name="Crunches", value="12x"))
            arrayList?.add(Exercise(name="Sit ups", value="14x"))
            arrayList?.add(Exercise(name="Stretches", value="40s"))
            arrayList?.add(Exercise(name="Squats", value="14x"))
            arrayList?.add(Exercise(name="Pullups", value="12x"))
            arrayList?.add(Exercise(name="Crunches", value="14x"))
            arrayList?.add(Exercise(name="Sit ups", value="8x"))
            arrayList?.add(Exercise(name="Stretches", value="40s"))
        }
        else
            return
    }
}