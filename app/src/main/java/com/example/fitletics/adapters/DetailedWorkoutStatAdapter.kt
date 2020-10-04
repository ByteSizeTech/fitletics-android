package com.example.fitletics.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.fitletics.R

class DetailedWorkoutStatAdapter(private val context: Activity) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.recent_exerceise_stats, null)

        return view
    }

    override fun getItem(p0: Int): Any {
        return 2
    }

    override fun getItemId(p0: Int): Long {
        return 2.toLong()
    }

    override fun getCount(): Int {
        return 20
    }
}