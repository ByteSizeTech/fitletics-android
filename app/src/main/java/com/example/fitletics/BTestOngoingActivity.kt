package com.example.fitletics

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_baseline_test_ongoing.*
import kotlinx.android.synthetic.main.activity_baseline_test_qr_scanner.*

class BTestOngoingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baseline_test_ongoing)


        //boiler plate code for progress bar below
        btest_ongoing_pb.max = 10
        val currentProgress = 10

        ObjectAnimator.ofInt(btest_ongoing_pb, "progress", currentProgress).setDuration(3500).start()

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the progress bar (for now)

        btest_ongoing_pb.setOnClickListener()
        {
            val intent = Intent(this, BTestCompleteActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/

    }
}