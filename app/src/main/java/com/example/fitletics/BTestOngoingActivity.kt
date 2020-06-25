package com.example.fitletics

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_baseline_test_ongoing.*

class BTestOngoingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baseline_test_ongoing)


        //boiler plate code for progress bar below
        progressBar.max = 10
        val currentProgress = 10

        ObjectAnimator.ofInt(progressBar, "progress", currentProgress).setDuration(3500).start()

    }
}