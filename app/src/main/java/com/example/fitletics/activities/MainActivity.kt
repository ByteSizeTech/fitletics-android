package com.example.fitletics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitletics.R
import com.example.fitletics.adapters.ViewPagerAdapter
import com.example.fitletics.fragments.AnalyticsFragment
import com.example.fitletics.fragments.DashboardFragment
import com.example.fitletics.fragments.SettingsFragment
import com.example.fitletics.fragments.WorkoutFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        start_workout_fab.setOnClickListener()
        {
            val intent = Intent(this, StartWorkoutActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/

        setupTabs()
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
