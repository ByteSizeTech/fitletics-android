package com.example.fitletics.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.fitletics.adapters.CustomExpandableListAdapter
import com.example.fitletics.R
import com.example.fitletics.activities.DetailedAnalyticsActivity
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout


class AnalyticsFragment : Fragment() {

    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: CustomExpandableListAdapter? = null
    internal var titleList: List<String> ? = null

    val data: HashMap<String, List<Workout>>
        get() {
            val listData = HashMap<String, List<Workout>>()

            val tempList: ArrayList<Exercise> = ArrayList()

            val categoriesList = ArrayList<Workout>()
            categoriesList.add(Workout("Arms", tempList, "easy", "20 mins"))
            categoriesList.add(Workout("Legs", tempList, "easy", "20 mins"))
            categoriesList.add(Workout("Core", tempList, "easy", "20 mins"))
            listData["Categories"] = categoriesList

            val exercisesList = ArrayList<Workout>()
            exercisesList.add(Workout("Pushups", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Pullups", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Squats", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Lunges", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Crunches", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Plank", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Burpees", tempList, "easy", "20 mins"))
            exercisesList.add(Workout("Jumping Jacks", tempList, "easy", "20 mins"))
            listData["Exercises"] = exercisesList

            return listData
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_analytics, container, false)

        Log.d("DEBUG", "Reached here!")
        expandableListViewCode = rootView.findViewById(R.id.expandableListViewFragmentAnalytics)
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
                val intent = Intent(this.activity!!, DetailedAnalyticsActivity::class.java)
                startActivity(intent)
                false
            }
        }

        return rootView
    }
}