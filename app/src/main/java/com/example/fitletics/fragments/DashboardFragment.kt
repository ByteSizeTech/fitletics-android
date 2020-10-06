package com.example.fitletics.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.fitletics.R
import com.example.fitletics.adapters.DashboardAnalyticsAdapter
import com.example.fitletics.models.DashboardAnalyticsItem


class DashboardFragment : Fragment() {


    private var arrayList: ArrayList<DashboardAnalyticsItem>? = null
    private var gridView: GridView? = null
    private var dashboardAnalyticsAdapter: DashboardAnalyticsAdapter? = null

    private var userNameText: TextView? = null
    private var userXpText: TextView? = null
    private var userProgressBar: ProgressBar? = null
    private var userProgressText: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        gridView = rootView.findViewById(R.id.grid_list)
        arrayList = ArrayList()
        arrayList = setDataList()
        dashboardAnalyticsAdapter = DashboardAnalyticsAdapter(this.activity!!.applicationContext, arrayList!!)
        gridView?.adapter = dashboardAnalyticsAdapter

        setupUserDetails(rootView)

        return rootView
    }

    private fun setupUserDetails(view : View){

    }

    private fun setDataList() : ArrayList<DashboardAnalyticsItem>{
        var arrayList: ArrayList<DashboardAnalyticsItem> = ArrayList()

        arrayList.add(DashboardAnalyticsItem("Walking Distance", "1.5 KM", R.color.color1 ))
        arrayList.add(DashboardAnalyticsItem("Calories Burned", "172 Kcal", R.color.color2))
        arrayList.add(DashboardAnalyticsItem("Workout Duration", "75 mins", R.color.color3))


        return arrayList
    }
}