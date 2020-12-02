package com.example.fitletics.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.fitletics.R
import com.example.fitletics.models.Exercise
import kotlinx.android.synthetic.main.activity_active_session.*

class ActiveSessionActivity : AppCompatActivity() {

    private var exUnitIsRep: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_session)

        val exerciseProgressLinearLayout = findViewById<LinearLayout>(R.id.exercise_progress_linear_layout)

//        mapOf(
//            ""
//        )

        session_current_exercise_name_tv.setOnClickListener {
            exUnitIsRep = !exUnitIsRep
            if (exUnitIsRep){
                exerciseProgressLinearLayout.removeAllViewsInLayout()
                layoutInflater.inflate(R.layout.rep_count_layout, exerciseProgressLinearLayout)
            }
            else{
                exerciseProgressLinearLayout.removeAllViewsInLayout()
                layoutInflater.inflate(R.layout.exercise_secs_progress_bar_layout, exerciseProgressLinearLayout)
            }
        }




    }
}