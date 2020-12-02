package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.dialogs.ExerciseDescriptionDialog
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_shared_workout.*

class SharedWorkoutActivity : AppCompatActivity() {

    private var workoutObject: Workout? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null


    private val TAG = "SHARED_WORKOUT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_workout)

        workoutObject = intent.getSerializableExtra("Workout_object") as Workout
        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        setupButtons()
        setupArrayList()
        setupActivityText()

        listView = findViewById(R.id.shared_workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, workoutObject?.exerciseList!!)
        listView?.adapter = listAdapter
        listView?.setOnItemClickListener{parent, view, position, id ->
            val entry= parent.getItemAtPosition(position) as Exercise
            Log.d(TAG, "desc: ${entry.name}");
            val dialog = ExerciseDescriptionDialog(entry)
            dialog.show(supportFragmentManager, "exerciseDescription")
        }

    }

    private fun setupButtons() {
        shared_accept_button.setOnClickListener {
            var tempWorkoutObject = Workout(
                name= workoutObject?.name,
                exerciseList= workoutObject?.exerciseList!!,
                difficulty=workoutObject?.difficulty,
                time=workoutObject?.time
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
                    Log.d(TAG, "Workout added to database")
                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                        .collection("Workouts")
                        .document("Pending")
                        .collection("workouts")
                        .document(workoutObject?.id!!)
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
                .document(workoutObject?.id!!)
                .delete()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupActivityText() {
        Log.d(TAG, "name: ${workoutObject?.name}, time: ${workoutObject?.time}")

        val nameText: TextView = findViewById(R.id.shared_workout_title_text)
        val timeText: TextView = findViewById(R.id.shared_workout_time_text)

        nameText.text = workoutObject?.name
        timeText.text = workoutObject?.time
    }

    private fun setupArrayList() {
        if (workoutObject?.exerciseList?.isNullOrEmpty()!!){
            Log.d(TAG, "Exercise list was empty!")
            workoutObject?.exerciseList = ArrayList()
            workoutObject?.exerciseList?.add(Exercise(name="Crunches", value="5x"))
            workoutObject?.exerciseList?.add(Exercise(name="Sit ups", value="8x"))
            workoutObject?.exerciseList?.add(Exercise(name="Strtches", value="10x"))
            workoutObject?.exerciseList?.add(Exercise(name="Squats", value="3x"))
            workoutObject?.exerciseList?.add(Exercise(name="Pullups", value="7x"))
        }
        else
            return
    }
}