package com.example.fitletics.activities.analytics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.adapters.DetailedWorkoutStatAdapter
import com.example.fitletics.models.support.Analytic
import com.example.fitletics.models.support.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_detailed_analytics.*
import kotlin.collections.ArrayList


class DetailedAnalyticsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listAdapter: DetailedWorkoutStatAdapter

    private var analyticList : ArrayList<Analytic> = ArrayList()
    private lateinit var calList: Array<DataPoint>
    private lateinit var valList: Array<DataPoint>
    private lateinit var timeList: Array<DataPoint>


    val testAnalytics : ArrayList<Analytic> = ArrayList()

    private val TAG = "Detailed_Analytics"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_analytics)

        val intent = intent
        Log.d("ANALYTIC_NAME_GET", "Name: ${intent.getStringExtra("Analytic name")}")


        listView = findViewById(R.id.recent_exercise_stat_list)

        if (intent.getStringExtra("Analytic name") == "Push Up") {      //MOCK DATA if push up is selected
            testAnalytics.add(Analytic("8/12/20", 12.toString(), 8.toString()))
            testAnalytics.add(Analytic("8/12/20", 12.toString(), 8.toString()))
            testAnalytics.add(Analytic("9/12/20", 10.toString(), 6.toString()))
            testAnalytics.add(Analytic("10/12/20", 12.toString(), 7.toString()))
            testAnalytics.add(Analytic("11/12/20", 12.toString(), 7.toString()))
            testAnalytics.add(Analytic("12/12/20", 12.toString(), 6.toString()))
            testAnalytics.add(Analytic("12/12/20", 13.toString(), 6.toString()))
            testAnalytics.add(Analytic("13/12/20", 14.toString(), 6.toString()))
            testAnalytics.add(Analytic("14/12/20", 15.toString(), 7.toString()))
            testAnalytics.add(Analytic("15/12/20", 16.toString(), 7.toString()))
            testAnalytics.add(Analytic("16/12/20", 16.toString(), 7.toString()))
            testAnalytics.add(Analytic("17/12/20", 20.toString(), 14.toString()))
            testAnalytics.add(Analytic("18/12/20", 20.toString(), 13.toString()))
            testAnalytics.add(Analytic("19/12/20", 20.toString(), 12.toString()))
            testAnalytics.add(Analytic("19/12/20", 20.toString(), 11.toString()))
            testAnalytics.add(Analytic("20/12/20", 23.toString(), 11.toString()))
            testAnalytics.add(Analytic("21/12/20", 23.toString(), 11.toString()))
            testAnalytics.add(Analytic("22/12/20", 23.toString(), 10.toString()))
            testAnalytics.add(Analytic("23/12/20", 23.toString(), 10.toString()))
            testAnalytics.add(Analytic("24/12/20", 25.toString(), 10.toString()))
            testAnalytics.add(Analytic("25/12/20", 25.toString(), 9.toString()))
            testAnalytics.add(Analytic("26/12/20", 25.toString(), 9.toString()))

            analyticList = testAnalytics


            listAdapter = DetailedWorkoutStatAdapter(this, analyticList)
            listView?.adapter = listAdapter
            setupGraphVariables(isReversed = false)
            setupButtonListeners()
        }
        else{
            setupActivityText(intent.getStringExtra("Analytic name"))
            setupAnalyticsfromDatabase(intent.getStringExtra("Analytic name"))
        }

    }

    private fun setupButtonListeners() {

        valsButton.setOnClickListener{
            graph.removeAllSeries()
            setupGraph(valList)
        }

        timeButton.setOnClickListener{
            graph.removeAllSeries()
            setupGraph(timeList)
        }


        favorite_toggle.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked)
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                    .collection("Analytics")
                    .document(intent.getStringExtra("Analytic name")!!)
                    .set(mapOf("favorite" to true))

            else
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                    .collection("Analytics")
                    .document(intent.getStringExtra("Analytic name")!!)
                    .set(mapOf("favorite" to false))
        }
    }

    private fun setupGraphVariables(isReversed: Boolean = true) {

        val averagedAnalytic: ArrayList<Analytic> = ArrayList()

        val similarDateList = analyticList.groupBy{
            it.date
        }
        Log.d(TAG, "AVERAGED_VAL: similar dates -> ${similarDateList}")


        var tempRepList : ArrayList<Int> = ArrayList()
        val tempTTakenList : ArrayList<Int> = ArrayList()
        for (similardate in similarDateList.iterator()){
            for (analytic in similardate.value){
                tempRepList.add(analytic.value!!.toInt())
                tempTTakenList.add(analytic.time!!.toInt())
            }
            val repAvg = tempRepList.sum() / tempRepList.size
            Log.d(TAG, "AVERAGED_VAL: repavg -> ${tempRepList.sum()} / ${tempRepList.size}" )
            val timeAvg = tempTTakenList.sum() / tempTTakenList.size
            val tempAnalytic = Analytic(similardate.key, timeAvg.toString(), repAvg.toString())
            averagedAnalytic.add(tempAnalytic)
            Log.d(TAG, "AVERAGED_VAL: ${tempAnalytic.date}, ${tempAnalytic.value}, ${tempAnalytic.time}\n" )
            tempRepList.clear()
            tempTTakenList.clear()
        }

        if (isReversed) {
            valList = averagedAnalytic.map {
                DataPoint(
                    it.date!!.split("/")[0].toDouble(),
                    it.value!!.split(" ")[0].toDouble()
                )
            }.toTypedArray().reversedArray()

            timeList = averagedAnalytic.map {
                DataPoint(
                    it.date!!.split("/")[0].toDouble(),
                    it.time!!.split(" ")[0].toDouble()
                )
            }.toTypedArray().reversedArray()
        }else{
            valList = averagedAnalytic.map {
                DataPoint(
                    it.date!!.split("/")[0].toDouble(),
                    it.value!!.split(" ")[0].toDouble()
                )
            }.toTypedArray()

            timeList = averagedAnalytic.map {
                DataPoint(
                    it.date!!.split("/")[0].toDouble(),
                    it.time!!.split(" ")[0].toDouble()
                )
            }.toTypedArray()
        }

        setupGraph(valList)
    }

    private fun setupGraph(graphVals: Array<DataPoint>) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(graphVals)
        graph.addSeries(series)
        series.color = Color.parseColor("#ffffff")
    }

    private fun setupAnalyticsfromDatabase(key: String?) {
        var dbAnalyticList: List<Map<String, String>> = ArrayList()

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Analytics")
            .document(key!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.data != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${doc.data}")
                    for (analytic in doc!!.data!!) {
                        if (analytic.key == "analytics")
                            dbAnalyticList = analytic.value as List<Map<String, String>>
                        if (analytic.key == "favorite") {
                            if (analytic.value as Boolean)
                                favorite_toggle.isChecked = true
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "$key does not exist as favorites")
            }

        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("WorkoutSession")
            .orderBy("dateCompleted", Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .addOnCompleteListener { documentSnapshot ->
                val results = documentSnapshot.result?.documents

                if (results != null) {
                    for (entry in results) {
                        val dateCompleted = entry["dateCompleted"] as String?
                        val completedStat = entry["completedStats"] as Map<String, Any?>

                        for (workout in completedStat){
//                            Log.d(TAG, "endtries: ${workout.value}")
                            val stat = workout.value as Map<String, Any?>
                            if(stat["exerciseName"] == key) {
                                val tempAnalytic = Analytic(
                                    dateCompleted,
                                    stat["timeTaken"].toString(),
                                    stat["repsDone"].toString()
                                )
                            analyticList.add(tempAnalytic)
                            }
                        }
                    }
                }
                listAdapter = DetailedWorkoutStatAdapter(this, analyticList)
                listView?.adapter = listAdapter
                setupGraphVariables()
                setupButtonListeners()
            }

    }

    private fun setupActivityText(name: String?) {
        val nameText: TextView = findViewById(R.id.analytics_title)
        nameText.text = name
    }
}