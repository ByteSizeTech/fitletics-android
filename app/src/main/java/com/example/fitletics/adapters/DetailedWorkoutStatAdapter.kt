package com.example.fitletics.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.models.Analytic
import kotlin.text.split

class DetailedWorkoutStatAdapter(private val context: Activity,
                                 private val analyticList: ArrayList<Analytic>) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.recent_exerceise_stats, null)

        val dateText = view.findViewById<TextView>(R.id.stat_date)
        val timeText = view.findViewById<TextView>(R.id.stat_time)
        val calsText = view.findViewById<TextView>(R.id.stst_cals)
        val valsValueText = view.findViewById<TextView>(R.id.stat_val)
        val valsUnitText = view.findViewById<TextView>(R.id.stat_val_unit)

        dateText.text = analyticList[p0].date
        timeText.text = analyticList[p0].time!!.split(" ")[0]
        calsText.text = analyticList[p0].cals!!.split(" ")[0]
        valsValueText.text = analyticList[p0].value!!.split(" ")[0]
        valsUnitText.text = analyticList[p0].value!!.split(" ")[1]

        return view
    }

    override fun getItem(p0: Int): Any {
        return analyticList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return analyticList.size
    }
}