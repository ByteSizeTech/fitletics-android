package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.fitletics.R
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.support.Muscle
import com.example.fitletics.models.support.Workout
import com.example.fitletics.models.utils.WebsiteSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_body_analysis_complete.*

class BAnalysisCompleteActivity : AppCompatActivity() {

    val TAG = "AnalysisCompleteActivity"

    lateinit var BLT_WORKOUT: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_analysis_complete)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        banalysis_complete_done.setOnClickListener()
        {
            val intent = Intent(this, BTestOngoingActivity::class.java)
            startActivity(intent)
        }

        checkUserMuscleScore()

        /*===============================================*/
    }

    private fun checkUserMuscleScore() {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .get()
            .addOnCompleteListener { doc ->
                Log.d(TAG, "document being get is ${doc.result?.get("coreScore")}")
                if (doc.result?.get("coreScore") == 0.0){
                    Log.d(TAG, "reached inside the if")
                    getBLTWorkout()
                }
            }
    }

    private fun getBLTWorkout() {
        Log.d(TAG, "called func")
        FirebaseFirestore.getInstance()
            .collection("BLT_REF")
            .document("Baseline_Test_Exercise")
            .get()
            .addOnSuccessListener {
                val exerciseDB = it.data?.get("exerciseList") as ArrayList<HashMap<String, Any>>
                val exercises: ArrayList<Exercise> = ArrayList()
                for (exercise in exerciseDB) {
                    var timePerRep: Double?
                    val unit =
                        if (exercise["unit"] == Exercise.Unit.REPS || exercise["unit"] == "REPS") {
                            Exercise.Unit.REPS
                        } else {
                            Exercise.Unit.SECS
                        }

                    timePerRep = exercise["timePerRep"].toString().toDoubleOrNull()

                    val tempExerciseObject =
                        Exercise(
                            name = exercise["name"].toString(),
                            description = exercise["description"].toString(),
                            link = exercise["link"].toString(),
                            difficulty = exercise["difficulty"].toString(),
                            unit = unit as Exercise.Unit?
                        )
                    tempExerciseObject.timePerRep = timePerRep
                    tempExerciseObject.value = exercise["value"].toString().toInt()
                    exercises.add(tempExerciseObject)

                    val muscleListDB = exercise["targetMuscles"] as ArrayList<HashMap<String, Any?>>
                    val targetedMuscles: ArrayList<Muscle> = ArrayList()
                    for (muscle in muscleListDB) {
                        val muscleName = muscle["name"].toString()
                        val male_exer = muscle["maleIntensity"].toString().toInt()
                        val fem_exer = muscle["femaleIntensity"].toString().toInt()

                        targetedMuscles.add(
                            Muscle(
                                muscleName,
                                male_exer,
                                fem_exer
                            )
                        )
                    }
                    tempExerciseObject.targetMuscles = targetedMuscles
//                Log.d(TAG, "exerciseList: ${it.result?.get("exerciseList")}")
                }
                val tempWorkoutObject =
                    Workout(
                        name = it.data?.get("name").toString(),
                        exerciseList = exercises,
                        time = it.data?.get("time").toString(),
                        difficulty = it.data?.get("difficulty").toString()
                    )
//                Log.d(TAG, "from here: ${tempWorkoutObject.exerciseList}")
                BLT_WORKOUT = tempWorkoutObject
                Log.d(TAG, "setup workout obj: $BLT_WORKOUT")

                WebsiteSession(this, BTestOngoingActivity::class.java, BLT_WORKOUT);
            }
    }
}