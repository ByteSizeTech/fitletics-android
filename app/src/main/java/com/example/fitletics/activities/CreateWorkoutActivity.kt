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
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.support.Muscle
import com.example.fitletics.models.support.Workout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create_workout.*

class CreateWorkoutActivity : AppCompatActivity() {


    val exerList: ArrayList<Exercise> = ArrayList()
    var selectedExercise: Int? = null;

    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    private val TAG = "CREATE_WORKOUT_ACT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        setupExerciseList()

        listView = findViewById(R.id.create_workout_exercise_list)

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

                        val targetedMuscles: ArrayList<Muscle>? = ArrayList();
                        for (i  in 0..2){
                            val muscleName =  exercise.child("targeted_muscle").child(i.toString()).child("name").value.toString()
                            val male_exer =  Integer.parseInt(exercise.child("targeted_muscle").child(i.toString()).child("male_exer").value.toString())
                            val fem_exer =  Integer.parseInt(exercise.child("targeted_muscle").child(i.toString()).child("fem_exer").value.toString())

                            targetedMuscles?.add(
                                Muscle(
                                    muscleName,
                                    male_exer,
                                    fem_exer
                                )
                            )
                            Log.d(TAG, "name of muscle: ${targetedMuscles?.get(i)?.name} " +
                                    "fem_exer: ${targetedMuscles?.get(i)?.femaleIntensity}," +
                                    "male:exer: ${targetedMuscles?.get(i)?.maleIntensity}  from ex: $name" )
                        }
                        var timePerRep: Double? = null
                        //TODO: Add easier/harder exercise to object and in the done button function
                        if (unit == Exercise.Unit.REPS){
                            timePerRep = exercise.child("timeforrep").value.toString().toDoubleOrNull()
                            Log.d("VAL_TEST", "time per rep for $name is $timePerRep")
                        }
                        val tempExerciseObject=
                            Exercise(
                                name = name.toString(),
                                description = description.toString(),
                                link = link.toString(),
                                difficulty = difficulty.toString(),
                                unit = unit,
                                targetMuscles = targetedMuscles,
                                timePerRep = timePerRep
                            )
                        Log.d("EXERCISE_LIST",
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
            val tempExerciseObject =
                Exercise(
                    name = exerList[selectedExercise!!].name,
                    description = exerList[selectedExercise!!].description,
                    link = exerList[selectedExercise!!].link,
                    difficulty = exerList[selectedExercise!!].difficulty,
                    unit = exerList[selectedExercise!!].unit
                )

            tempExerciseObject.targetMuscles = exerList[selectedExercise!!].targetMuscles
            tempExerciseObject.value = value.toInt()
            tempExerciseObject.timePerRep = exerList[selectedExercise!!].timePerRep

            createdExerciseList.add(tempExerciseObject)
            listAdapter = WorkoutExerciseListAdapter(this, createdExerciseList!!)
            listView?.adapter = listAdapter
        }
        doneButton()
    }

    private fun doneButton(){
        create_workout_done_button.setOnClickListener {
            var tempWorkoutObject = Workout(
                name = workout_title_text.text.toString(),
                exerciseList = this.createdExerciseList,
                difficulty = "TBD",
                time = "TBD"
            )

            tempWorkoutObject.calculateDuration();
            tempWorkoutObject.calculateDifficulty();

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

            //Temp code used to quickly create variants of BLT workouts
//            FirebaseFirestore.getInstance()
//                .collection("BLT_REF")
//                .document("Baseline_Test_Exercise")
//                .set(tempWorkoutObject)
//                .addOnSuccessListener {
//                    Log.d("WORKOUT STATUS", "Workout added to database")
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                }
        }
    }

}