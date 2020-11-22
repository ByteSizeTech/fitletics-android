package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.User
import com.example.fitletics.models.Workout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create_workout.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.example.fitletics.activities.CreateWorkoutActivity as CreateWorkoutActivity

class CreateWorkoutActivity : AppCompatActivity() {


    val exerList: ArrayList<Exercise> = ArrayList()
    var selectedExercise: Int? = null;

    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //populate firebase with exercises (preferebly JSON so easy to edit)
        //replace w spinner (make spinner accept exercises)
        //add selected exerciese to list
        //add workout to database


        //MAKE GOOGLE SHEETS WITH PENDING WORK


        setupExerciseList()

        listView = findViewById(R.id.create_workout_exercise_list)


//        val workouts = arrayOf(Workout("Number uno", tempList, "Easy", "378 mins"),
//            Workout("Numero deaux", tempList, "Easy", "456 mins"),
//            Workout("theesra", tempList, "Easy", "347 mins"),
//            Workout("arbaaa", tempList, "Easy", "16 mins"))

        val stringss = arrayOf("Lmao", "lol", "lel")


        create_workout_spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,  exerList.map { it.name }
        )

    }

    private fun setupExerciseList() {
        //getValue(Exercise::class.java)!!
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (exercise in p0.children){
                        val name = exercise.child("name").value
                        val description = exercise.child("description").value
                        val difficulty = exercise.child("difficulty").value
                        val link = exercise.child("link").value
                        val unit = exercise.child("unit").getValue(Exercise.Unit::class.java)
                        val tempExerciseObject= Exercise(
                            name = name.toString(),
                            description = description.toString(),
                            link = link.toString(),
                            difficulty = difficulty.toString(),
                            unit = unit
                        )
                        Log.d("EXERCISE LIST",
                            "name: ${tempExerciseObject.name}, description: ${tempExerciseObject.description}, unit: ${tempExerciseObject.unit}")
                        exerList.add(tempExerciseObject)
                    }
                    create_workout_spinner.adapter = ArrayAdapter(
                        this@CreateWorkoutActivity, android.R.layout.simple_spinner_item,  exerList.map { it.name }
                    )
                    saveSelectedExercise()
                    addButton()
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    private fun saveSelectedExercise() {
        create_workout_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedExercise = p2
                Log.d("SELECTED EXER", "$selectedExercise")
            }

        }
    }

    private val createdExerciseList: ArrayList<Exercise> = ArrayList()

    private fun addButton() {
        create_add_workout.setOnClickListener {
            var value = exercise_value_textview.text.toString()
            if (exerList[selectedExercise!!].unit == Exercise.Unit.REPS)
                value += 'x'
            else
                value += 's'
            val tempExerciseObject =Exercise(
                exerList[selectedExercise!!].name,
                exerList[selectedExercise!!].description,
                exerList[selectedExercise!!].link,
                exerList[selectedExercise!!].difficulty,
                exerList[selectedExercise!!].unit)

            tempExerciseObject.value = value
            createdExerciseList.add(tempExerciseObject)
            listAdapter = WorkoutExerciseListAdapter(this, createdExerciseList!!)
            listView?.adapter = listAdapter
        }
        doneButton()
    }

    private fun doneButton(){
        create_workout_done_button.setOnClickListener {
            var tempWorkoutObject = Workout(
                name= workout_title_text.text.toString(),
                exerciseList= this.createdExerciseList,
                difficulty="TBD",
                time="TBD"
            )

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                .collection("Workouts")
                .document("Custom")
                .collection("workouts")
                .document()
                .set(tempWorkoutObject, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("WORKOUT STATUS", "Workout added to database")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }

}