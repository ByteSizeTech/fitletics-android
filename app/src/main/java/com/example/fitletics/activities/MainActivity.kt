package com.example.fitletics.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
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
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {

    val mDatabase = FirebaseDatabase.getInstance()

    val TAG = "MAIN_ACTIVITY"


    class MyWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

        object WorkManagerScheduler {

            fun refreshPeriodicWork(context: Context) {

                val currentDate = Calendar.getInstance()
                val dueDate = Calendar.getInstance()

                // Set Execution around 07:00:00 AM
                dueDate.set(Calendar.HOUR_OF_DAY, 12)
                dueDate.set(Calendar.MINUTE, 0)
                dueDate.set(Calendar.SECOND, 0)
                if (dueDate.before(currentDate)) {
                    dueDate.add(Calendar.HOUR_OF_DAY, 24)
                }

                val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff)

                Log.d("MyWorker", "time difference $minutes")

                //define constraints
                val myConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val refreshCpnWork = PeriodicWorkRequest.Builder(MyWorker::class.java, 24, TimeUnit.HOURS)
                    .setInitialDelay(minutes, TimeUnit.MINUTES)
                    .setConstraints(myConstraints)
                    .addTag("myWorkManager")
                    .build()


                WorkManager.getInstance(context).enqueueUniquePeriodicWork("myWorkManager",
                    ExistingPeriodicWorkPolicy.REPLACE, refreshCpnWork)

            }
        }

        override suspend fun doWork(): Result {
            return try {
                try {
                    Log.d("MyWorker", "Run work manager")
                    //Do Your task here
                    doYourTask()
                    Result.success()
                } catch (e: Exception) {
                    Log.d("MyWorker", "exception in doWork ${e.message}")
                    Result.failure()
                }
            } catch (e: Exception) {
                Log.d("MyWorker", "exception in doWork ${e.message}")
                Result.failure()
            }
        }

        private fun doYourTask() {
            Log.d("MyWorker", "task Running!")
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.navigationBarColor = resources.getColor(R.color.tabColor)

        Log.d("MyWorker", "Launching periodic func")
        MyWorker.WorkManagerScheduler.refreshPeriodicWork(this)

        setupTabs()
        setupFab()
        startPedometer()
    }

    private var sensorManager: SensorManager? = null;

    private var running = false;
    private var totalSteps = 0f;
    private var previousTotalSteps = 0f;

    private val STEP_PERMISSION_CODE = 1;

    private var step_track: String? = null

    private fun startPedometer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("SensorChange", "Sensor Permission Needed!");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    STEP_PERMISSION_CODE);
            }
        }else
        {
            Log.d("SensorChange", "Sensor Permission NOT Needed!");
        }

        loadData();
        resetSteps();
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
    }

    override fun onResume() {
        super.onResume()
        running = true;
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            Toast.makeText(this, "No sensor hardware detected!", Toast.LENGTH_SHORT);
        }
        else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d("SensorChange", "Sensor Activated.");
        }
    }

    override fun onPause() {
        super.onPause()
        running = false;
        sensorManager?.unregisterListener(this);
        Log.d("SensorChange", "Sensor Deactivated.");
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("SensorChange", "running is false and function entered.");
        if(running) {
            totalSteps = event!!.values[0];
            Log.d("SensorChange", "running is true and block entered: $totalSteps");
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt();
            Constants.STEP_COUNT = ("$currentSteps steps");
        }
    }

    fun resetSteps() {
        start_workout_fab.setOnLongClickListener{
            previousTotalSteps = totalSteps;
            Constants.STEP_COUNT = "0 steps";
            saveData();
            true
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit();
        editor.putFloat("key1", previousTotalSteps);
        editor.apply();
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        val savedNumber = sharedPreferences.getFloat("key1", 0f);
        Log.d("MainActivity", "Number saved to preferences: $savedNumber");
        previousTotalSteps = savedNumber;
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
