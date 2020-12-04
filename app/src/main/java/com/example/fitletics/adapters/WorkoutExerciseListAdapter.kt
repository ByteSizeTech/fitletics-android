package com.example.fitletics.adapters

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.models.support.Exercise

class WorkoutExerciseListAdapter(private val context: Activity,
                                 private val arrayList: ArrayList<Exercise>) : BaseAdapter()  {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.workout_list_element, null)

        val statText = view.findViewById<TextView>(R.id.statText)
        val exerciseNameText = view.findViewById<TextView>(R.id.exerciseNameText)

        if(arrayList[p0].unit == Exercise.Unit.REPS) {
            Log.d("Exercise_List_Adapter", "isreps")
            statText.text = "${arrayList[p0].value.toString()}x"
        }
        else if (arrayList[p0].unit == Exercise.Unit.SECS) {
            Log.d("Exercise_List_Adapter", "issecs")
            statText.text = "${arrayList[p0].value.toString()}s"
        }
        else{
            Log.d("Exercise_List_Adapter", "WRONG")
            statText.text = "WRONG"
        }
        exerciseNameText.text = arrayList[p0].name

        return view
    }

    override fun getItem(p0: Int): Any {
        return arrayList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
}