package com.example.fitletics.fragments.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.fitletics.R
import com.example.fitletics.activities.DetailedAnalyticsActivity
import com.example.fitletics.adapters.DashboardAnalyticsAdapter
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.misc.DashboardAnalyticsItem
import com.example.fitletics.models.support.Analytic
import com.example.fitletics.models.support.User
import com.example.fitletics.models.utils.UserXP
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.analytics_dashboard_card.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {

    private var dashboardAnalyticsArrayList: ArrayList<DashboardAnalyticsItem>? = null
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
                    val xp = (snapshot.data?.get("xp") as Long).toInt()
                    val bodyTypefromDB = snapshot.data?.get("bodyType") as String?

                    var bodyType: User.BodyType? = null
                    when(bodyTypefromDB){
                        "ENDOMORPHIC" -> bodyType = User.BodyType.ENDOMORPHIC
                        "ECTOMORPHIC" -> bodyType = User.BodyType.ECTOMORPHIC
                        "MESOMORPHIC" -> bodyType = User.BodyType.MESOMORPHIC
                        null -> bodyType = User.BodyType.MESOMORPHIC
                    }
                    Constants.CURRENT_USER =
                        User(
                            userID,
                            name,
                            email,
                            DOB,
                            gender,
                            weight.toInt(),
                            height.toInt(),
                            bodyType,
                            xp
                        )
                    setupUserDetails()
                }
            }
    }

    private fun setupUserDetails(){
        user_name_text.text = Constants.CURRENT_USER!!.name
        user_id_text.text = Constants.CURRENT_USER!!.userID

        val user_Xp =  UserXP.returnExperienceProgress(Constants.CURRENT_USER!!.xp!!)
        Log.d("USERXP", "$user_Xp")

        user_progress_bar.progress = user_Xp["Percentage"] as Int
        user_progress_text.text = user_Xp["XP"] as String
        user_xp_text.text = "Level ${user_Xp["Level"]}"
    }

    var favoriteAnalyticList : ArrayList<String> = ArrayList()
    private fun setupFavoriteAnalytics(){
        dashboardAnalyticsArrayList = ArrayList()
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
                                favoriteAnalyticList?.add(workout.id)
                                setupFavoriteAnalyticValue(workout.id)
                            }}}}
                dashboardAnalyticsAdapter = DashboardAnalyticsAdapter(this.activity!!.applicationContext, dashboardAnalyticsArrayList!!)
                gridView?.adapter = dashboardAnalyticsAdapter

            }
    }

    private fun setupFavoriteAnalyticValue(exerciseName : String) {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("WorkoutSession")
            .orderBy("dateCompleted", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnCompleteListener { documentSnapshot ->
                val results = documentSnapshot.result?.documents

                if (results != null) {
                    for (entry in results) {
                        val dateCompleted = entry["dateCompleted"] as String?
                        val completedStat = entry["completedStats"] as Map<String, Any?>

                        for (workout in completedStat) {
                            val stat = workout.value as Map<String, Any?>
                            if (stat["exerciseName"] == exerciseName) {
                                val dashboardItem : DashboardAnalyticsItem
                                if(exerciseName == "Plank" || exerciseName == "Wallsit"){
                                    val index = ((0..10).random()).toLong()
                                    dashboardItem  = DashboardAnalyticsItem(
                                        exerciseName,
                                        "${stat["repsDone"].toString()} seconds",
                                        index
                                    )
                                }else{
                                    val index = ((0..10).random()).toLong()
                                    dashboardItem  = DashboardAnalyticsItem(
                                        exerciseName,
                                        "${stat["repsDone"].toString()} reps",
                                        index
                                    )
                                }

                                dashboardAnalyticsArrayList?.add(dashboardItem)
                            }
                        }
                    }
                    dashboardAnalyticsAdapter = DashboardAnalyticsAdapter(this.activity!!.applicationContext, dashboardAnalyticsArrayList!!)
                    gridView?.adapter = dashboardAnalyticsAdapter
                }
            }
    }

    private fun setDataList(){
        dashboardAnalyticsArrayList?.add(
            DashboardAnalyticsItem(
                "Steps Walked",
                Constants.STEP_COUNT,
                R.color.color1
            )
        )
        dashboardAnalyticsArrayList?.add(
            DashboardAnalyticsItem(
                "Calories Burned",
                "0 Kcal",
                R.color.color2
            )
        )
        dashboardAnalyticsArrayList?.add(
            DashboardAnalyticsItem(
                "Workout Duration",
                "0 mins",
                R.color.color3
            )
        )
        getDatabaseFavoriteAnalytics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("DASHFRAG","onDestroy!")
    }
}