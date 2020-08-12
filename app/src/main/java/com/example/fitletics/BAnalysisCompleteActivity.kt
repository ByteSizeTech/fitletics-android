package com.example.fitletics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_body_analysis_complete.*
import kotlinx.android.synthetic.main.activity_body_analysis_ongoing.*

class BAnalysisCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_analysis_complete)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        banalysis_complete_done.setOnClickListener()
        {
            val intent = Intent(this, BTestOngoingActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/
    }
}