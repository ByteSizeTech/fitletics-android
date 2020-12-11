package com.example.fitletics.fragments.homepage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import com.example.fitletics.adapters.WorkoutsExpandableListAdapter
import com.example.fitletics.R
import com.example.fitletics.activities.*
import com.example.fitletics.fragments.homepage.dialogs.ExerciseDescriptionDialog
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.support.Muscle
import com.example.fitletics.models.support.Workout
import com.example.fitletics.models.utils.WebsiteSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class WorkoutFragment : Fragment() {

    private val TAG = "Workout_Fragment"
    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: WorkoutsExpandableListAdapter? = null
    internal var titleList: List<String> ? = null


    val customWorkouts = ArrayList<Workout>()
    val pendingWorkouts = ArrayList<Workout>()

    val tempList2: ArrayList<Exercise> = ArrayList()

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    val data: LinkedHashMap<String, List<Workout>>
        get() {

            val listData = LinkedHashMap<String, List<Workout>>()
            val tempList: ArrayList<Exercise> = ArrayList()

            tempList2.add(
                Exercise(
                    name = "Squats",
                    value = 7
                )
            )
            tempList2.add(
                Exercise(
                    name = "Lunges",
                    value = 14
                )
            )
            tempList2.add(
                Exercise(
                    name = "Push ups",
                    value = 20
                )
            )
            tempList2.add(
                Exercise(
                    name = "Stretches",
                    value = 40
                )
            )

            getWorkouts()

            val standardWorkouts = ArrayList<Workout>()
            val fullBodyWorkout: Workout = Workout()
            fullBodyWorkout.name = "FullBody"
            val UpperWorkout: Workout = Workout()
            UpperWorkout.name = "Upper"
            val LowerWorkout: Workout = Workout()
            LowerWorkout.name = "Lower"
            val CoreWorkout: Workout = Workout()
            CoreWorkout.name = "Core"
            standardWorkouts.add(fullBodyWorkout)
            standardWorkouts.add(UpperWorkout)
            standardWorkouts.add(LowerWorkout)
            standardWorkouts.add(CoreWorkout)

            listData["Standard"] = standardWorkouts
            listData["Custom"] = customWorkouts
            listData["Pending"] = pendingWorkouts

            return listData
        }

    var count = 0

    private fun getWorkouts() {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Workouts")
            .document("Custom")
            .collection("workouts")
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d("SNAPSHOT METADATA", "${snapshot}")
                    customWorkouts.clear()  //first clear the list

                    snapshot.documents.forEach {
                        //this is cuz data is stored in an array of maps {0:{}, 1:{}}
                        val exercisesDB = it.get("exerciseList") as ArrayList<HashMap<String, Any?>>
                        val exercises: ArrayList<Exercise> = ArrayList()
                        for (exercise in exercisesDB){

                            var timePerRep: Double?
                            val unit =
                                if (exercise["unit"] == Exercise.Unit.REPS || exercise["unit"] == "REPS") {
                                    Exercise.Unit.REPS
//                                    timePerRep = exercise["timePerRep"].toString().toDoubleOrNull()
                                }
                                else {
                                    Exercise.Unit.SECS
//                                    timePerRep = null
                                }

                            timePerRep = exercise["timePerRep"].toString().toDoubleOrNull()

                            val tempExerciseObject=
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
                            for (muscle in muscleListDB){
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
                        }
                        val tempWorkoutObject =
                            Workout(
                                name = it.get("name").toString(),
                                exerciseList = exercises,
                                time = it.get("time").toString(),
                                difficulty = it.get("difficulty").toString()
                            )
                        tempWorkoutObject.id=it.id
                        Log.d("WORKOUT ID", "${tempWorkoutObject.id}")

                        customWorkouts.add(tempWorkoutObject)
                    }
                }
            }

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Workouts")
            .document("Pending")
            .collection("workouts")
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d("SNAPSHOT_METADATA", "${snapshot}")
                    pendingWorkouts.clear()  //first clear the list

                    snapshot.documents.forEach {
                        //this is cuz data is stored in an array of maps {0:{}, 1:{}}
                        val exercisesDB = it.get("exerciseList") as ArrayList<HashMap<String, Any?>>
                        val exercises: ArrayList<Exercise> = ArrayList()
                        for (exercise in exercisesDB){
                            var timePerRep: Double?
                            val unit =
                                if (exercise["unit"] == Exercise.Unit.REPS || exercise["unit"] == "REPS") {
                                    Exercise.Unit.REPS
//                                    timePerRep = exercise["timePerRep"].toString().toDoubleOrNull()
                                }
                                else {
                                    Exercise.Unit.SECS
//                                    timePerRep = null
                                }

                            timePerRep = exercise["timePerRep"].toString().toDoubleOrNull()

                            val tempExerciseObject=
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
                            for (muscle in muscleListDB){
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
                        }
                        val tempWorkoutObject =
                            Workout(
                                name = it.get("name").toString(),
                                exerciseList = exercises,
                                time = it.get("time").toString(),
                                difficulty = it.get("difficulty").toString()
                            )
                        tempWorkoutObject.id=it.id
                        Log.d("WORKOUT ID", "${tempWorkoutObject.id}")

                        pendingWorkouts.add(tempWorkoutObject)
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = this.activity!!.getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

    Log.d("WORKOUT FRAGMENT", "I am created!")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView: View = inflater.inflate(R.layout.fragment_workout, container, false)

        //TEMP TEST CODE *********************************
//        initializeTempButton(rootView.findViewById<Button>(R.id.create_workout_button_test))

        /*Changes intent to create workout*/
        rootView.findViewById<Button>(R.id.create_workout_button).setOnClickListener()
        {
            val intent2 = Intent(this.activity!!, CreateWorkoutActivity::class.java)
            startActivity(intent2)
        }
        /*===============================================*/


        Log.d("DEBUG", "Reached here!")
        expandableListViewCode = rootView.findViewById(R.id.expandableListViewFragmentWorkouts)

        if (expandableListViewCode != null) {
            val listData = data
            titleList = ArrayList(listData.keys)
            adapter = WorkoutsExpandableListAdapter(
                this.activity!!,
                titleList as ArrayList<String>,
                listData,
                expandableListViewCode
            )
            Log.d("DEBUG", "Activity: ${this.activity!!}")
            expandableListViewCode!!.setAdapter(adapter)


            expandableListViewCode!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

                if ((titleList as ArrayList<String>)[groupPosition] == "Pending"){
                    val tempWorkout =
                        Workout(
                            id = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].id,
                            name = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].name,
                            exerciseList = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].exerciseList!!,
                            difficulty = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].difficulty,
                            time = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].time
                        )
                    val intent = Intent(this.activity!!, SharedWorkoutActivity::class.java)
                    intent.putExtra("Workout_object", tempWorkout)
                    startActivity(intent)
                }


                else if (!sessionUID.isNullOrEmpty()) {
                    Log.d(TAG, "checking active session...")
                    FirebaseFirestore.getInstance()
                        .collection("Sessions")
                        .document(sessionUID!!)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                if (document.data?.get("active_task") == "SD" && document.data?.get("task_state") == "ongoing") {
                                    Log.d(TAG, "document: ${document.data?.get("active_task")}")
                                    Log.d(TAG, "document: ${document.data?.get("task_state")}")

                                    val dialog =
                                        ExerciseDescriptionDialog(
                                            this.activity!!,
                                            "Ongoing Session!",
                                            "There is an on-going session!" +
                                                    "\nYou must end the active session in order to start another activity that requires the website. " +
                                                    "\n\nDo you want to end the active session?",
                                            "Continue Session", "End Session", false
                                        )
                                    dialog.show(this.activity!!.supportFragmentManager, "exerciseDescription")
                                }
                                else{
                                    launchWorkoutDescription(groupPosition, childPosition)
                                }
                            }
                        }
                }
                else{
                    launchWorkoutDescription(groupPosition, childPosition)
                }
                false
            }
        }
        return rootView
    }

    private fun launchWorkoutDescription(groupPosition: Int, childPosition: Int) {
        Log.d(TAG, "The_position_is: ${(titleList as ArrayList<String>)[groupPosition]}")


        if((titleList as ArrayList<String>)[groupPosition] == "Standard"){
            val intentClass = StartStandardWorkoutActivity::class.java
            val tempStandardWorkoutWorkout = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition]
            WebsiteSession(
                this.activity!!,
                intentClass,
                tempStandardWorkoutWorkout
            );
        }

        else{
            val tempWorkout =
                Workout(
                    id = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].id,
                    name = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].name,
                    exerciseList = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].exerciseList!!,
                    difficulty = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].difficulty,
                    time = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].time
                )
            val intentClass = StartCustomWorkoutActivity::class.java
            tempWorkout.calculateDifficulty()
            tempWorkout.calculateDuration()
            WebsiteSession(
                this.activity!!,
                intentClass,
                tempWorkout
            );
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}