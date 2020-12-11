package com.example.fitletics.fragments.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.fitletics.R
import com.example.fitletics.activities.DetailedAnalyticsActivity
import com.example.fitletics.adapters.AnalyticsExpandableListAdapter
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.support.Muscle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class AnalyticsFragment : Fragment() {

    internal var expandableListViewCode: ExpandableListView? = null
    internal var adapter: AnalyticsExpandableListAdapter? = null
    internal var titleList: List<String> ? = null

    val TAG = "ANALYTICS_FRAGMENT"

    val exercisesList = ArrayList<String>()

    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val tempList: ArrayList<Exercise> = ArrayList()

            getAllExercises()

            listData["Exercises"] = exercisesList

            return listData
        }


    private fun getAllExercises() {
        FirebaseDatabase
            .getInstance()
            .reference
            .child("Exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    exercisesList.clear()
                    for (exercise in p0.children) {
                        val name = exercise.child("name").value
                        exercisesList.add(name.toString())
                    }
                    Log.d(TAG, "exercList: $exercisesList")
                }
            })
    }
    private fun getExerciseAnalyticsKeys() {
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
            adapter = AnalyticsExpandableListAdapter(
                this.activity!!,
                titleList as ArrayList<String>,
                listData,
                expandableListViewCode
            )
            Log.d("DEBUG", "Activity: ${this.activity!!}")
            expandableListViewCode!!.setAdapter(adapter)


            expandableListViewCode!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                val intent = Intent(this.activity!!, DetailedAnalyticsActivity::class.java)
                intent.putExtra("Analytic name", data[(titleList as ArrayList<String>)[groupPosition]]!![childPosition])
                startActivity(intent)
                false
            }
        }

        return rootView
    }
}