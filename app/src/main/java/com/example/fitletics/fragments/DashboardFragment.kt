package com.example.fitletics.fragments

import android.app.Person
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import com.example.fitletics.R
import com.example.fitletics.activities.CreateWorkoutActivity
import com.example.fitletics.adapters.DashboardAnalyticsAdapter
import com.example.fitletics.models.Constants
import com.example.fitletics.models.DashboardAnalyticsItem
import com.example.fitletics.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {


    private var arrayList: ArrayList<DashboardAnalyticsItem>? = null
    private var gridView: GridView? = null
    private var dashboardAnalyticsAdapter: DashboardAnalyticsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DASHFRAG","onCreate!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        Log.d("DASHFRAG","onCreateView!")
        gridView = rootView.findViewById(R.id.grid_list)

        setupDatabase()

        return rootView
    }

    private fun setupDatabase() {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Details")
            .document("details")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Constants.CURRENT_USER = snapshot.toObject(User::class.java)
                    setupUserDetails()
                }
            }
    }

    private fun setupUserDetails(){
        user_name_text.text = Constants.CURRENT_USER!!.name
    }

    private fun setupFavoriteAnalytics(){
        arrayList = ArrayList()
        arrayList = setDataList()
        dashboardAnalyticsAdapter = DashboardAnalyticsAdapter(this.activity!!.applicationContext, arrayList!!)
        gridView?.adapter = dashboardAnalyticsAdapter
    }

    private fun setDataList() : ArrayList<DashboardAnalyticsItem>{
        var arrayList: ArrayList<DashboardAnalyticsItem> = ArrayList()

        arrayList.add(DashboardAnalyticsItem("Walking Distance", "1.5 KM", R.color.color1 ))
        arrayList.add(DashboardAnalyticsItem("Calories Burned", "172 Kcal", R.color.color2))
        arrayList.add(DashboardAnalyticsItem("Workout Duration", "75 mins", R.color.color3))

        return arrayList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("DASHFRAG","onDestroy!")
    }
}