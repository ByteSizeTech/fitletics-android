package com.example.fitletics.activities

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
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_detailed_analytics.*


class DetailedAnalyticsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listAdapter: DetailedWorkoutStatAdapter

    private var analyticList : ArrayList<Analytic> = ArrayList()
    private lateinit var calList: Array<DataPoint>
    private lateinit var valList: Array<DataPoint>
    private lateinit var timeList: Array<DataPoint>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_analytics)

        val intent = intent
        Log.d("ANALYTIC_NAME_GET", "Name: ${intent.getStringExtra("Analytic name")}")

        setupActivityText(intent.getStringExtra("Analytic name"))
        setupAnalyticsfromDatabase(intent.getStringExtra("Analytic name"))

        listView = findViewById(R.id.recent_exercise_stat_list)

    }

    private fun setupButtonListeners() {
        calsButton.setOnClickListener{
            graph.removeAllSeries()
            setupGraph(calList)
        }

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
                    .update("favorite", true)

            else
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                    .collection("Analytics")
                    .document(intent.getStringExtra("Analytic name")!!)
                    .update("favorite", false)
        }
    }

    private fun setupGraphVariables() {
        calList = analyticList.map {
            DataPoint(it.date!!.split("/")[0].toDouble(),
                it.cals!!.split(" ")[0].toDouble())
        }.toTypedArray()

        Log.d("CAL_LIST", "$calList")

        valList = analyticList.map {
            DataPoint(it.date!!.split("/")[0].toDouble(),
                it.value!!.split(" ")[0].toDouble())
        }.toTypedArray()
        timeList = analyticList.map {
            DataPoint(it.date!!.split("/")[0].toDouble(),
                it.time!!.split(" ")[0].toDouble())
        }.toTypedArray()

        setupGraph(calList)
    }

    private fun setupGraph(graphVals: Array<DataPoint>) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(graphVals)
        graph.addSeries(series)
        series.color = Color.parseColor("#ffffff")
    }

    private fun setupAnalyticsfromDatabase(key: String?) {
        var dbAnalyticList: List<Map<String, String>> = ArrayList()
//        FirebaseFirestore.getInstance()
//            .collection("Users")
//            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
//            .collection("Analytics")
//            .document(key!!)
//            .addSnapshotListener { snapshot, e ->
//                if (e != null){
//                    Log.w("ERROR", "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//                snapshot!!.data?.entries?.forEach {
//                    analyticList = it.value as List<Map<String, String>>
//                }
//                Log.d("DB_ANALYTIC_REP", "DocumentSnapshot data: ${(analyticList[0]["value"])}")
//            }

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .collection("Analytics")
            .document(key!!)
            .get()
            .addOnSuccessListener { doc ->
            if (doc != null) {
                //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                for (analytic in doc!!.data!!){
                    if(analytic.key == "analytics")
                        dbAnalyticList = analytic.value as List<Map<String, String>>
                    if(analytic.key == "favorite"){
                        if (analytic.value as Boolean)
                            favorite_toggle.isChecked = true
                    }
                }
                dbAnalyticList.forEach {
                    val tempAnalytic =
                        Analytic(
                            date = it["date"],
                            time = it["time"],
                            calories = it["cals"],
                            value = it["value"]
                        )
                    analyticList.add(tempAnalytic)
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