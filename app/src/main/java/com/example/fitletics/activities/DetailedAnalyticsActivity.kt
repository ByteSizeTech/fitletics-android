package com.example.fitletics.activities

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.adapters.DetailedWorkoutStatAdapter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_detailed_analytics.*


class DetailedAnalyticsActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var listAdapter: DetailedWorkoutStatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_analytics)

        val graph = findViewById(R.id.graph) as GraphView
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)
            )
        )
        series.title = "Reps"
        graph.addSeries(series)

        calsButton.setOnClickListener{
            graph.removeAllSeries()
            val series: LineGraphSeries<DataPoint> = LineGraphSeries(
                arrayOf(
                    DataPoint(0.0, 2.0),
                    DataPoint(1.0, 8.0),
                    DataPoint(2.0, 3.0),
                    DataPoint(3.0, 6.0),
                    DataPoint(4.0, 9.0)
                )
            )
            series.color = Color.parseColor("#ffffff")
            series.title = "Calories"
            graph.addSeries(series)
        }

        listView = findViewById(R.id.recent_exercise_stat_list)
        listAdapter = DetailedWorkoutStatAdapter(this)
        listView?.adapter = listAdapter

    }
}