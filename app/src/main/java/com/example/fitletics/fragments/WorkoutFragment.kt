package com.example.fitletics.fragments

import android.content.Intent
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
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.WebsiteSession
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class WorkoutFragment : Fragment() {

    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: WorkoutsExpandableListAdapter? = null
    internal var titleList: List<String> ? = null


    val customWorkouts = ArrayList<Workout>()
    val pendingWorkouts = ArrayList<Workout>()

    val tempList2: ArrayList<Exercise> = ArrayList()

    val data: LinkedHashMap<String, List<Workout>>
        get() {


            val listData = LinkedHashMap<String, List<Workout>>()
            val tempList: ArrayList<Exercise> = ArrayList()

            tempList2.add(Exercise(name="Squats", value="7x"))
            tempList2.add(Exercise(name="Lunges", value="14x"))
            tempList2.add(Exercise(name="Push ups", value="20x"))
            tempList2.add(Exercise(name="Stretches", value="40s"))


            getWorkouts()

            //pendingWorkouts.add(Workout("Beginner run", tempList2, "Easy", "40 mins"))
//            pendingWorkouts.add(Workout("Try this", tempList, "Medium", "80 mins"))
//            getWorkouts()

            val savedWorkouts = ArrayList<Workout>()
            savedWorkouts.add(Workout("Full Body", tempList, "Hard", "120 mins"))
            savedWorkouts.add(Workout("Upper Body", tempList, "Easy", "20 mins"))
            savedWorkouts.add(Workout("Core", tempList, "Hard", "120 mins"))
            savedWorkouts.add(Workout("Lower Body", tempList, "Hard", "120 mins"))

            listData["Standard"] = savedWorkouts
            listData["Custom"] = customWorkouts
            listData["Pending"] = pendingWorkouts

            return listData
        }

    var count = 0
    private fun initializeTempButton(button: Button) {
        button.setOnClickListener {
            count++
            var tempWorkoutObject = Workout(
                name = "workout${count}",
                exerciseList = this.tempList2,
                difficulty = "TBD",
                time = "TBD"
            )

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                .collection("Workouts")
                .document("Pending")
                .collection("workouts")
                .document()
                .set(tempWorkoutObject, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("WORKOUT STATUS", "Workout added to database")
                }

            adapter = WorkoutsExpandableListAdapter(
                this.activity!!,
                titleList as ArrayList<String>,
                data,
                expandableListViewCode
            )
            Log.d("DEBUG", "Activity: ${this.activity!!}")
            expandableListViewCode!!.setAdapter(adapter)
        }
    }

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
                            val unit =
                                if (exercise["unit"] == Exercise.Unit.REPS)
                                    Exercise.Unit.REPS
                                else
                                    Exercise.Unit.SECS   //no idea why this was necessary

                            val tempExerciseObject= Exercise(
                                name = exercise["name"].toString(),
                                description = exercise["description"].toString(),
                                link = exercise["link"].toString(),
                                difficulty = exercise["difficulty"].toString(),
                                unit = unit as Exercise.Unit?)

                            tempExerciseObject.value = exercise["value"] as String?
                            exercises.add(tempExerciseObject)
                        }
                        val tempWorkoutObject = Workout(
                            name= it.get("name").toString(),
                            exerciseList= exercises,
                            time = it.get("time").toString(),
                            difficulty = it.get("difficulty").toString())
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
                    Log.d("SNAPSHOT METADATA", "${snapshot}")
                    pendingWorkouts.clear()  //first clear the list

                    snapshot.documents.forEach {
                        //this is cuz data is stored in an array of maps {0:{}, 1:{}}
                        val exercisesDB = it.get("exerciseList") as ArrayList<HashMap<String, Any?>>
                        val exercises: ArrayList<Exercise> = ArrayList()
                        for (exercise in exercisesDB){
                            val unit =
                                if (exercise["unit"] == Exercise.Unit.REPS)
                                    Exercise.Unit.REPS
                                else
                                    Exercise.Unit.SECS   //no idea why this was necessary

                            val tempExerciseObject= Exercise(
                                name = exercise["name"].toString(),
                                description = exercise["description"].toString(),
                                link = exercise["link"].toString(),
                                difficulty = exercise["difficulty"].toString(),
                                unit = unit as Exercise.Unit?)

                            tempExerciseObject.value = exercise["value"] as String?
                            exercises.add(tempExerciseObject)
                        }
                        val tempWorkoutObject = Workout(
                            name= it.get("name").toString(),
                            exerciseList= exercises,
                            time = it.get("time").toString(),
                            difficulty = it.get("difficulty").toString())
                        tempWorkoutObject.id=it.id
                        Log.d("WORKOUT ID", "${tempWorkoutObject.id}")

                        pendingWorkouts.add(tempWorkoutObject)
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    Log.d("WORKOUT FRAGMENT", "I am created!")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView: View = inflater.inflate(R.layout.fragment_workout, container, false)

        initializeTempButton(rootView.findViewById<Button>(R.id.create_workout_button_test))  //TEMP TEST CODE *********************************

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
                Log.d("WTF IS THIS?", "It is: ${(titleList as ArrayList<String>)[groupPosition]}")
                val intent: Intent
                val intentClass: Class<*>
                if ((titleList as ArrayList<String>)[groupPosition] == "Pending") {
                    intent = Intent(this.activity!!, SharedWorkoutActivity::class.java)
                    intentClass = SharedWorkoutActivity::class.java
                }
                else {
                    intent = Intent(this.activity!!, StartWorkoutActivity::class.java)
                    intentClass = StartWorkoutActivity::class.java
                }
                val tempWorkout = Workout(
                    id = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].id,
                    name = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].name,
                    exerciseList = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].exerciseList!!,
                    difficulty = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].difficulty,
                    time = data[(this.titleList as ArrayList<String>)[groupPosition]]!![childPosition].time
                )

                WebsiteSession(this.activity!!, intentClass, tempWorkout);


//                intent.putExtra("Workout_object", tempWorkout)

//                startActivity(intent)
                false
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}