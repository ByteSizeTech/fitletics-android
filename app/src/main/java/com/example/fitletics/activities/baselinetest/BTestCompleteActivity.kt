package com.example.fitletics.activities.baselinetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitletics.R
import com.example.fitletics.activities.main.MainActivity
import kotlinx.android.synthetic.main.activity_baseline_test_complete.*

class BTestCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baseline_test_complete)

        lets_go_button.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}