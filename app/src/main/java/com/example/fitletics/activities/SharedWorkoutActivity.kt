package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create_workout.*
import kotlinx.android.synthetic.main.activity_shared_workout.*

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

        setupButtons()

        listView = findViewById(R.id.shared_workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, arrayList!!)
        listView?.adapter = listAdapter
        listView?.setOnItemClickListener{parent, view, position, id ->
            //TODO: Finish this
        }

    }

    private fun setupButtons() {
        shared_accept_button.setOnClickListener {
            var tempWorkoutObject = Workout(
                name= intent.getStringExtra("Workout_name")!!,
                exerciseList= arrayList!!,
                difficulty="TBD",
                time="TBD"
            )
            tempWorkoutObject.id = null

            //add workout to custom document
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                .collection("Workouts")
                .document("Custom")
                .collection("workouts")
                .document()
                .set(tempWorkoutObject, SetOptions.merge())
                .addOnCompleteListener {
                    Log.d("WORKOUT STATUS", "Workout added to database")
                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                        .collection("Workouts")
                        .document("Pending")
                        .collection("workouts")
                        .document(intent.getStringExtra("Workout_id")!!)
                        .delete().addOnCompleteListener {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                }




        }
        shared_decline_button.setOnClickListener {
            //delete workout to pending document
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                .collection("Workouts")
                .document("Pending")
                .collection("workouts")
                .document(intent.getStringExtra("Workout_id")!!)
                .delete()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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
            arrayList?.add(Exercise(name="Crunches", value="5x"))
            arrayList?.add(Exercise(name="Sit ups", value="8x"))
            arrayList?.add(Exercise(name="Strtches", value="10x"))
            arrayList?.add(Exercise(name="Squats", value="3x"))
            arrayList?.add(Exercise(name="Pullups", value="7x"))
        }
        else
            return
    }
}