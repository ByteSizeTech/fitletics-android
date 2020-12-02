package com.example.fitletics.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.fitletics.R
import com.example.fitletics.activities.ConnectPCQRActivity
import com.example.fitletics.activities.DetailedAnalyticsActivity
import com.example.fitletics.adapters.DashboardAnalyticsAdapter
import com.example.fitletics.models.Constants
import com.example.fitletics.models.DashboardAnalyticsItem
import com.example.fitletics.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.analytics_dashboard_card.view.*
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
        setupFavoriteAnalytics()
        setupGridViewLayout()

        return rootView
    }

    private fun setupGridViewLayout(){
        gridView?.setOnItemClickListener { parent, view, position, id ->
            if(position > 2) {
                Log.d("GRID_ON_CLICK", "${position}")
                val intent = Intent(this.activity!!, DetailedAnalyticsActivity::class.java)
                intent.putExtra("Analytic name", view.analytics_dashboard_title.text)
                startActivity(intent)
            }
        }
    }

    private fun setupDatabase() {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userID = snapshot.data?.get("userID") as String
                    val name = snapshot.data?.get("name") as String
                    val email = snapshot.data?.get("email") as String
                    val gender = snapshot.data?.get("gender") as String
                    val DOB = snapshot.data?.get("dob") as String
                    val weight = snapshot.data?.get("weight") as Long
                    val height = snapshot.data?.get("height") as Long
                    val bodyTypefromDB = snapshot.data?.get("bodyType") as String?

                    var bodyType: User.BodyType? = null
                    when(bodyTypefromDB){
                        "ENDOMORPHIC" -> bodyType = User.BodyType.ENDOMORPHIC
                        "ECTOMORPHIC" -> bodyType = User.BodyType.ECTOMORPHIC
                        "MESOMORPHIC" -> bodyType = User.BodyType.MESOMORPHIC
                        null -> bodyType = User.BodyType.MESOMORPHIC
                    }
                    Constants.CURRENT_USER = User(
                        userID,
                        name,
                        email,
                        DOB,
                        gender,
                        weight.toInt(),
                        height.toInt(),
                        bodyType
                    )
                    setupUserDetails()
                }
            }
    }

    private fun setupUserDetails(){
        user_name_text.text = Constants.CURRENT_USER!!.name
        user_id_text.text = Constants.CURRENT_USER!!.userID
    }

    private fun setupFavoriteAnalytics(){
        arrayList = ArrayList()
        setDataList()
    }

    private fun getDatabaseFavoriteAnalytics() {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Analytics")
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }
                snapshot!!.documents?.forEach {workout ->
                    workout.data?.entries?.forEach { value ->
                        if(value.key == "favorite"){
                            if (value.value as Boolean) {
                                arrayList?.add(DashboardAnalyticsItem(
                                    workout.id,
                                    (workout.data as Map<String, List<Map<String, String>>>)["analytics"]?.last()?.get("value"),
                                    R.color.color1 ))
                            }
                        }
                    }
                }
                dashboardAnalyticsAdapter = DashboardAnalyticsAdapter(this.activity!!.applicationContext, arrayList!!)
                gridView?.adapter = dashboardAnalyticsAdapter
            }
    }

    private fun setDataList(){
        arrayList?.add(DashboardAnalyticsItem("Steps Walked", Constants.STEP_COUNT, R.color.color1 ))
        arrayList?.add(DashboardAnalyticsItem("Calories Burned", "0 Kcal", R.color.color2))
        arrayList?.add(DashboardAnalyticsItem("Workout Duration", "0 mins", R.color.color3))
        getDatabaseFavoriteAnalytics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("DASHFRAG","onDestroy!")
    }
}