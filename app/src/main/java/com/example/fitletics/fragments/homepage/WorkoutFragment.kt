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

            //pendingWorkouts.add(Workout("Beginner run", tempList2, "Easy", "40 mins"))
//            pendingWorkouts.add(Workout("Try this", tempList, "Medium", "80 mins"))
//            getWorkouts()

            val savedWorkouts = ArrayList<Workout>()
            savedWorkouts.add(
                Workout(
                    "Full Body",
                    tempList,
                    "Hard",
                    "120 mins"
                )
            )
            savedWorkouts.add(
                Workout(
                    "Upper Body",
                    tempList,
                    "Easy",
                    "20 mins"
                )
            )
            savedWorkouts.add(
                Workout(
                    "Core",
                    tempList,
                    "Hard",
                    "120 mins"
                )
            )
            savedWorkouts.add(
                Workout(
                    "Lower Body",
                    tempList,
                    "Hard",
                    "120 mins"
                )
            )

            listData["Standard"] = savedWorkouts
            listData["Custom"] = customWorkouts
            listData["Pending"] = pendingWorkouts

            return listData
        }

    var count = 0
//    private fun initializeTempButton(button: Button) {
//        button.setOnClickListener {
//            count++
//            var tempWorkoutObject = Workout(
//                name = "workout${count}",
//                exerciseList = this.tempList2,
//                difficulty = "TBD",
//                time = "TBD"
//            )
//
//            FirebaseFirestore.getInstance()
//                .collection("Users")
//                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
//                .collection("Workouts")
//                .document("Pending")
//                .collection("workouts")
//                .document()
//                .set(tempWorkoutObject, SetOptions.merge())
//                .addOnSuccessListener {
//                    Log.d("WORKOUT STATUS", "Workout added to database")
//                }
//
//            adapter = WorkoutsExpandableListAdapter(
//                this.activity!!,
//                titleList as ArrayList<String>,
//                data,
//                expandableListViewCode
//            )
//            Log.d("DEBUG", "Activity: ${this.activity!!}")
//            expandableListViewCode!!.setAdapter(adapter)
//        }
//    }

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

//            for (groups in 0 until listData.size){
//                expandableListViewCode!!.expandGroup(groups);
//            }

//            expandableListViewCode!!.setOnGroupExpandListener { groupPosition -> Toast.makeText(this.activity?.applicationContext, (titleList as ArrayList<String>)[groupPosition] + " List Expanded.", Toast.LENGTH_SHORT).show() }
//
//             expandableListViewCode!!.setOnGroupCollapseListener { groupPosition -> Toast.makeText(this.activity?.applicationContext, (titleList as ArrayList<String>)[groupPosition] + " List Collapsed.", Toast.LENGTH_SHORT).show() }
//
            expandableListViewCode!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
//                Toast.makeText(this.activity?.applicationContext, "Clicked: " + (titleList as ArrayList<String>)[groupPosition] + " -> " + listData[(titleList as ArrayList<Workout>)[groupPosition]]!!.get(childPosition),
//                    Toast.LENGTH_SHORT).show()


                if (!sessionUID.isNullOrEmpty()) {
                    Log.d(TAG, "checking active session...")
                    FirebaseFirestore.getInstance()
                        .collection("Sessions")
                        .document(sessionUID!!)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                if (document.data?.get("active_task") == "AS" && document.data?.get("task_state") == "ongoing") {
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



//                Log.d(TAG, "It is: ${(titleList as ArrayList<String>)[groupPosition]}")





//                intent.putExtra("Workout_object", tempWorkout)


//                startActivity(intent)
                false
            }
        }
        return rootView
    }

    private fun launchWorkoutDescription(groupPosition: Int, childPosition: Int) {
        Log.d(TAG, "Reached else..")

        val tempWorkout =
            Workout(
                id = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].id,
                name = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].name,
                exerciseList = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].exerciseList!!,
                difficulty = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].difficulty,
                time = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].time
            )
        if ((titleList as ArrayList<String>)[groupPosition] == "Pending"){
            val intent = Intent(this.activity!!, SharedWorkoutActivity::class.java)
            intent.putExtra("Workout_object", tempWorkout)
            startActivity(intent)
        }
        else{
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