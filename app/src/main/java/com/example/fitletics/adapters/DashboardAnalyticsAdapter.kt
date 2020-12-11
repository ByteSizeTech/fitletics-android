package com.example.fitletics.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.fitletics.R
import com.example.fitletics.models.misc.DashboardAnalyticsItem

class DashboardAnalyticsAdapter(var context: Context, var arrayList: ArrayList<DashboardAnalyticsItem>) :BaseAdapter() {

    private var count: Int = 0

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view:View
        view = View.inflate(context, R.layout.analytics_dashboard_card, null)

        val titleText: TextView = view.findViewById(R.id.analytics_dashboard_title)
        val statText: TextView = view.findViewById(R.id.analytics_dashboard_stats)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_root)

        val listItems: DashboardAnalyticsItem = arrayList.get(p0)

        titleText.text = listItems.title
        statText.text = listItems.stat
        relativeLayout.setBackgroundColor(ContextCompat.getColor(p2!!.getContext(), listItems.color!!));
        return view
    }

    override fun getItem(p0: Int): Any {
        return arrayList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
}