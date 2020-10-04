package com.example.fitletics.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.models.Exercise

class WorkoutExerciseListAdapter(private val context: Activity,  private val arrayList: ArrayList<Exercise>) : BaseAdapter()  {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var inflater = context.layoutInflater
        var view = inflater.inflate(R.layout.workout_list_element, null)

        var statText = view.findViewById<TextView>(R.id.statText)
        var exerciseNameText = view.findViewById<TextView>(R.id.exerciseNameText)

        statText.text = arrayList[p0].value
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