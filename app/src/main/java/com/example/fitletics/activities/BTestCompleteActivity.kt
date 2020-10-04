package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitletics.R
import kotlinx.android.synthetic.main.activity_baseline_test_complete.*

class BTestCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baseline_test_complete)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the progress bar (for now)

        lets_go_button.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/
    }
}