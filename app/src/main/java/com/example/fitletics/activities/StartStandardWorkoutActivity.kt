package com.example.fitletics.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.fragments.homepage.dialogs.ExerciseDescriptionDialog
import com.example.fitletics.models.support.*
import com.example.fitletics.models.utils.RecEngine
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_start_standard_workout.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StartStandardWorkoutActivity : AppCompatActivity() {

    private var workoutObject: Workout? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    val TAG = "START_WORKOUT_ACTIVITY"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_standard_workout)

        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        workoutObject = intent.getSerializableExtra("Workout_object") as Workout?
        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        begin_workout_button_standard_workout.isEnabled = false

        setupWorkout()
//        setWorkoutTexts()
//        populateWorkoutListView()

        begin_workout_button_standard_workout.setOnClickListener {
//            startActiveSessionActivity()
        }

        setupInitialScreen()

//        startSession()

    }

    private fun setupInitialScreen() {
        workout_title_text_standard_workout.text = "Generating Workout..."
        workout_time_text_standard_workout.text = ""
        workout_difficulty_text_standard_workout.text = ""
    }


    var lastSessionInCategory: Session? = null
    lateinit var lastSessionLevel: String
   @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWorkout() {
        getLastSessionInCategory()

    }

    private fun getLastSessionInCategory() {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)

            .collection("WorkoutSession")
//            .whereEqualTo("workout.name", "${workoutObject?.name}")
            .whereEqualTo("workout.name", "Core")
            .orderBy("dateCompleted", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnCompleteListener { documentSnapshot ->
                val result = documentSnapshot.result?.documents

                Log.d(TAG, "TELL_ME_WHAT_THIS_IS: ${result}")

                if (!result.isNullOrEmpty()){
                    val caloriesBurned = result.get(0).get("caloriesBurned")
//                    val dateCompleted = LocalDate.parse(result.get("dateCompleted").toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) as Date?
                    val timeTaken = result.get(0).get("timeTaken")
                    val workout = result.get(0).get("workout") as Map<String, Any?>
                    val completeddStats = result.get(0).get("completedStats") as Map<String, Any?>

                    val lastSessionInCategory: Session = Session(
                        caloriesBurned = caloriesBurned.toString().toFloatOrNull(),
                        timeTaken = timeTaken.toString().toFloatOrNull(),
                        workout = Workout(workout),
                        CompletedStats = completeddStats)

                    Log.d(TAG, "Last session result: ${lastSessionInCategory.caloriesBurned}, ${lastSessionInCategory.timeTaken}, ${lastSessionInCategory.CompletedStats},  ${lastSessionInCategory.workout}")
                }

                lastSessionInCategory = Session()

                getAllExercises()
            }
    }

    val exerciselist: ArrayList<Exercise> = ArrayList()
    private fun getAllExercises() {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    for (exercise in p0.children) {
                        val id = exercise.key
                        val name = exercise.child("name").value
                        val description = exercise.child("description").value
                        val difficulty = exercise.child("difficulty").value
                        val link = exercise.child("link").value
                        val unit = exercise.child("unit").getValue(Exercise.Unit::class.java)

                        val targetedMuscles: ArrayList<Muscle>? = ArrayList();
                        for (i in 0..2) {
                            val muscleName = exercise.child("targeted_muscle").child(i.toString())
                                .child("name").value.toString()
                            val male_exer = Integer.parseInt(
                                exercise.child("targeted_muscle").child(i.toString())
                                    .child("male_exer").value.toString()
                            )
                            val fem_exer = Integer.parseInt(
                                exercise.child("targeted_muscle").child(i.toString())
                                    .child("fem_exer").value.toString()
                            )

                            targetedMuscles?.add(
                                Muscle(
                                    muscleName,
                                    male_exer,
                                    fem_exer
                                )
                            )
                            Log.d(
                                TAG, "name of muscle: ${targetedMuscles?.get(i)?.name} " +
                                        "fem_exer: ${targetedMuscles?.get(i)?.femaleIntensity}," +
                                        "male:exer: ${targetedMuscles?.get(i)?.maleIntensity}  from ex: $name"
                            )
                        }
                        var timePerRep: Double? = null
                        //TODO: Add easier/harder exercise to object and in the done button function
                        if (unit == Exercise.Unit.REPS) {
                            timePerRep =
                                exercise.child("timeforrep").value.toString().toDoubleOrNull()
                            Log.d("VAL_TEST", "time per rep for $name is $timePerRep")
                        }
                        val tempExerciseObject =
                            Exercise(
                                name = name.toString(),
                                description = description.toString(),
                                link = link.toString(),
                                difficulty = difficulty.toString(),
                                unit = unit,
                                targetMuscles = targetedMuscles,
                                timePerRep = timePerRep
                            )
                        tempExerciseObject.id = id
                        Log.d(
                            "EXERCISE_LIST",
                            "name: ${tempExerciseObject.name}, description: ${tempExerciseObject.description}, unit: ${tempExerciseObject.unit}"
                        )
                        exerciselist.add(tempExerciseObject)
                    }

                    Log.d(TAG, "exercList: $exerciselist")
                    if (lastSessionInCategory?.workout?.level != null) {
                        lastSessionLevel = lastSessionInCategory!!.workout!!.level!!
                        getMilestoneExerciseVals(lastSessionLevel)
                    }
                    else {
                        lastSessionLevel = "Beginner"
                        getMilestoneExerciseVals(lastSessionLevel)
                    }
                }
            })
    }

    val milesStoneExerciseData: HashMap<String, Int?> = HashMap()
    private fun getMilestoneExerciseVals(sessionLevel: String = "Beginner") {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Milestones")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    var parsedName = workoutObject?.name?.toLowerCase()
                    if (workoutObject?.name == "FullBody") {
                        parsedName = "full"
                    }
                    var parsedLevel = sessionLevel.toLowerCase()

                    val category = parsedName
                    val level = parsedLevel

                    val exerciseVals = p0.child(category!!).child(level!!)

                    for (vals in exerciseVals.children){
                        milesStoneExerciseData[vals.key.toString()] = vals.value.toString().toIntOrNull()
                    }

                    Log.d(TAG, "exerciseVals: ${milesStoneExerciseData}")
                    getMilestoneWorkout()
                }
            })
    }

    val milestoneWorkout: Workout = Workout()
    private fun getMilestoneWorkout() {

        milestoneWorkout.name = workoutObject?.name
        milestoneWorkout.level = lastSessionLevel
        //TODO: Complete this object
        milestoneWorkout.exerciseList = exerciselist.filter {
            it.id in milesStoneExerciseData.keys
        } as ArrayList<Exercise>

//        Log.d(TAG, "milestoneExData: ${milestoneWorkout.exerciseList}")
        for (i in milestoneWorkout.exerciseList!!.indices){
            for (j in milesStoneExerciseData)
                if (j.key == milestoneWorkout.exerciseList!![i].id){
                    milestoneWorkout.exerciseList!![i].value = j.value
                }
            Log.d(TAG, "milestoneExData: ${milestoneWorkout.exerciseList!![i].id}: ${milestoneWorkout.exerciseList!![i].name},  ${milestoneWorkout.exerciseList!![i].value}")
        }

        setMileStoneWorkoutFromRecomender()
    }

    private fun setMileStoneWorkoutFromRecomender() {
        val recommendedWorkout = RecEngine.recommendWorkout(workoutObject?.name, milestoneWorkout, lastSessionInCategory)
        for (exercises in recommendedWorkout.exerciseList!!){
            Log.d(TAG, "FINAL_OUTPUT_WORKOUT: ${exercises.name}, ${exercises.value}, ${exercises.difficulty}")
        }
        recommendedWorkout.calculateDifficulty()
        recommendedWorkout.calculateDuration()
        workoutObject = recommendedWorkout
        Log.d(TAG, "difficylty: ${recommendedWorkout.difficulty}")

        populateWorkoutListView()
        setWorkoutTexts()
    }

    private fun setWorkoutTexts() {
        var workoutName: String = workoutObject?.name!!

        if (workoutName == "FullBody")
            workoutName = "Full Body"

        else if (workoutName == "Upper")
            workoutName = "Upper Body"

        else if (workoutName == "Lower")
            workoutName = "Lower Body"

        else if (workoutName == "Core")
            workoutName = "Core"

        else
            workoutName = "ERROR!"

        workout_title_text_standard_workout.text = workoutName
        workout_time_text_standard_workout.text = workoutObject?.time
        workout_difficulty_text_standard_workout.text = workoutObject?.difficulty
    }

    private fun populateWorkoutListView() {
        listView = findViewById(R.id.workout_exercise_list_standard_workout)
        listAdapter = WorkoutExerciseListAdapter(this, workoutObject?.exerciseList!!)
        listView?.adapter = listAdapter

        listView?.setOnItemClickListener{parent, view, position, id ->
            val entry= parent.getItemAtPosition(position) as Exercise
            Log.d(TAG, "desc: ${entry.name}")
            val dialog =
                ExerciseDescriptionDialog(
                    this,
                    entry.name!!,
                    entry.description,
                    entry.link!!,
                    "Cancel",
                    true
                )
            dialog.show(supportFragmentManager, "exerciseDescription")
        }
    }

//    private fun startSession() {
//    }
//
//    private fun startActiveSessionActivity() {
//    }
}