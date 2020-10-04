package com.example.fitletics.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ExpandableListView
import android.widget.Toast
import com.example.fitletics.adapters.CustomExpandableListAdapter
import com.example.fitletics.R
import com.example.fitletics.activities.SharedWorkoutActivity
import com.example.fitletics.activities.StartWorkoutActivity
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf


class WorkoutFragment : Fragment() {

    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: CustomExpandableListAdapter? = null
    internal var titleList: List<String> ? = null


    val data: HashMap<String, List<Workout>>
        get() {
            val listData = HashMap<String, List<Workout>>()

            val tempList: ArrayList<Exercise> = ArrayList()

            val customWorkouts = ArrayList<Workout>()
            customWorkouts.add(Workout("Please work", tempList, "Easy", "20 mins"))


            val tempList2: ArrayList<Exercise> = ArrayList()
            tempList2.add(Exercise("BLEH", "5x564"))
            tempList2.add(Exercise("Sit BLEH", "8x"))
            tempList2.add(Exercise("BLEH", "1000x"))
//            tempList2.add(Exercise("BLEH", "3x"))
//            tempList2.add(Exercise("Pullups", "7x"))
//            tempList2.add(Exercise("BLEH", "5x"))
//            tempList2.add(Exercise("Sit BLEH", "8x"))
//            tempList2.add(Exercise("BLEH", "10x"))
//            tempList2.add(Exercise("BLEH", "3000x"))
//            tempList2.add(Exercise("Pullups", "7x"))
            customWorkouts.add(Workout("Help", tempList, "Easy", "26 mins"))
            //Log.d("T_TEST", "ARRAY: ${customWorkouts[1].exerciseList!![0]}")
            customWorkouts.add(Workout("Last try", tempList, "Medium", "82 mins"))



            customWorkouts.add(Workout("Last last try", tempList, "Hard", "39 mins"))
            listData["Custom"] = customWorkouts

            val pendingWorkouts = ArrayList<Workout>()
            pendingWorkouts.add(Workout("Hercules", tempList2, "Easy", "40 mins"))
            pendingWorkouts.add(Workout("Percy Jackson", tempList, "Medium", "80 mins"))
            listData["Pending"] = pendingWorkouts

            val savedWorkouts = ArrayList<Workout>()
            savedWorkouts.add(Workout("Poseidon", tempList, "Hard", "120 mins"))
            savedWorkouts.add(Workout("Willy Wonka", tempList, "Easy", "20 mins"))
            listData["Saved"] = savedWorkouts

            return listData
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView: View = inflater.inflate(R.layout.fragment_workout, container, false)

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

            for (groups in 0 until listData.size){
                expandableListViewCode!!.expandGroup(groups);
            }

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