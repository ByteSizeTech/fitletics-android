package com.example.fitletics.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.adapters.ViewPagerAdapter
import com.example.fitletics.fragments.AnalyticsFragment
import com.example.fitletics.fragments.DashboardFragment
import com.example.fitletics.fragments.SettingsFragment
import com.example.fitletics.fragments.WorkoutFragment
import com.example.fitletics.models.Constants
import com.example.fitletics.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mDatabase = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupTabs()
        setupFab()
        startPedometer()
    }

    private fun startPedometer() {

    }

    private fun setupFab() {
        start_workout_fab.setOnClickListener()
        {
            val intent = Intent(this, StartWorkoutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTabs(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DashboardFragment(), "Dashboard")
        adapter.addFragment(WorkoutFragment(), "Workout")
        adapter.addFragment(AnalyticsFragment(), "Analytics")
        adapter.addFragment(SettingsFragment(), "Settings")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.home_icon)
        tabs.getTabAt(1)!!.setIcon(R.drawable.workout_whistle)
        tabs.getTabAt(2)!!.setIcon(R.drawable.analytics_icon)
        tabs.getTabAt(3)!!.setIcon(R.drawable.settings_icon)
    }
}
