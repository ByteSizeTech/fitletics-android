package com.example.fitletics.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import com.example.fitletics.adapters.CustomExpandableListAdapter
import com.example.fitletics.R
import com.example.fitletics.activities.*
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_workout.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf


class WorkoutFragment : Fragment() {

    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: CustomExpandableListAdapter? = null
    internal var titleList: List<String> ? = null


    val customWorkouts = ArrayList<Workout>()

    val tempList2: ArrayList<Exercise> = ArrayList()

    val data: HashMap<String, List<Workout>>
        get() {
            val listData = HashMap<String, List<Workout>>()

            val tempList: ArrayList<Exercise> = ArrayList()

            tempList2.add(Exercise(name="Squats", value="7x"))
            tempList2.add(Exercise(name="Lunges", value="14x"))
            tempList2.add(Exercise(name="Push ups", value="20x"))
            tempList2.add(Exercise(name="Stretches", value="40s"))


            getCustomWorkouts()
//            customWorkouts.add(Workout("First", tempList, "Easy", "20 mins"))
//            customWorkouts.add(Workout("Deux", tempList, "Easy", "26 mins"))
//            //Log.d("T_TEST", "ARRAY: ${customWorkouts[1].exerciseList!![0]}")
//            customWorkouts.add(Workout("Theesra", tempList, "Medium", "82 mins"))
//            customWorkouts.add(Workout("Last one", tempList, "Hard", "39 mins"))
            listData["Custom"] = customWorkouts

            val pendingWorkouts = ArrayList<Workout>()
            pendingWorkouts.add(Workout("Beginner run", tempList2, "Easy", "40 mins"))
            pendingWorkouts.add(Workout("Try this", tempList, "Medium", "80 mins"))
            listData["Pending"] = pendingWorkouts

            val savedWorkouts = ArrayList<Workout>()
            savedWorkouts.add(Workout("Arms Beginner", tempList, "Hard", "120 mins"))
            savedWorkouts.add(Workout("Core Beginner", tempList, "Easy", "20 mins"))
            listData["Saved"] = savedWorkouts

            return listData
        }

    private fun getCustomWorkouts() {
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
                    customWorkouts.clear()  //first clear the list
                    snapshot.documents.forEach {
                        val name = it.get("name")
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
                                unit = unit as Exercise.Unit?
                            )
                            tempExerciseObject.value = exercise["value"] as String?
                            exercises.add(tempExerciseObject)
                        }
                        val time = it.get("time")
                        val difficulty = it.get("difficulty")

                        Log.d("EXERLISTDB", "list: ${exercises as ArrayList<Exercise>} \n type: ${exercises.toString()} \n\n}")
                        Log.d("EXERLISTHC", "list: ${tempList2 as ArrayList<Exercise>} \n type: ${tempList2.toString()} \n\n}")

                        val tempWorkoutObject = Workout(
                            name= name.toString(),
                            exerciseList= exercises,
                            time = time.toString(),
                            difficulty = difficulty.toString()
                        )
                        customWorkouts.add(tempWorkoutObject!!)
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
            adapter = CustomExpandableListAdapter(
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
                var intent: Intent
                if ((titleList as ArrayList<String>)[groupPosition] == "Pending") {
                    intent = Intent(this.activity!!, SharedWorkoutActivity::class.java)
                }
                else {
                    intent = Intent(this.activity!!, StartWorkoutActivity::class.java)
                }
                var bundle: Bundle = Bundle();
                bundle.putSerializable("Workout_ex_list",
                    data[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition).exerciseList
                )
                //Log.d("ARGS", "gp: $groupPosition, cp: $childPosition, ld[gp]: ${listData[(titleList as ArrayList<String>)[groupPosition]]!!.get(groupPosition)}")
                intent.putExtra("Workout_name", data[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition).name)
                intent.putExtra("Workout_time", data[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition).time)
                intent.putExtra("Workout_diff", data[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition).difficulty)
                intent.putExtra("Workout_ex_list", data[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition).exerciseList )
                startActivity(intent);
                false
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}