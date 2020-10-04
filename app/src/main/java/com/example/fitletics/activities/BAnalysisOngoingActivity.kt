package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitletics.R
import kotlinx.android.synthetic.main.activity_body_analysis_ongoing.*

class BAnalysisOngoingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_analysis_ongoing)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        banalysis_ongoing_pb.setOnClickListener()
        {
            val intent = Intent(this, BAnalysisCompleteActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/
    }
}