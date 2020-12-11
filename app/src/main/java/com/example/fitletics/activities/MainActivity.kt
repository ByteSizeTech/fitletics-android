package com.example.fitletics.activities

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import com.example.fitletics.fragments.homepage.AnalyticsFragment
import com.example.fitletics.fragments.homepage.DashboardFragment
import com.example.fitletics.fragments.homepage.SettingsFragment
import com.example.fitletics.fragments.homepage.WorkoutFragment
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Session
import com.example.fitletics.models.support.Workout
import com.example.fitletics.models.utils.RecEngine
import com.example.fitletics.models.utils.WebsiteSession
import com.example.fitletics.models.utils.WorkoutPriority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import net.sourceforge.jFuzzyLogic.FIS
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {

    val mDatabase = FirebaseDatabase.getInstance()

    val TAG = "MAIN_ACTIVITY"

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences


    class MyWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

        object WorkManagerScheduler {

            fun refreshPeriodicWork(context: Context) {

                val currentDate = Calendar.getInstance()
                val dueDate = Calendar.getInstance()

                // Set Execution around 12:00:00 AM
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

        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)



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
            if (!sessionUID.isNullOrEmpty()) {
                Log.d(TAG, "checking active session...")
                FirebaseFirestore.getInstance()
                    .collection("Sessions")
                    .document(sessionUID!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            if ((document.data?.get("active_task") == "SD") && document.data?.get("task_state") == "ongoing") {
                                Log.d(TAG, "document: ${document.data?.get("active_task")}")
                                Log.d(TAG, "document: ${document.data?.get("task_state")}")

                                WebsiteSession(
                                    this,
                                    ActiveSessionActivity::class.java,
                                    null
                                );
                            }
                            else{
                                callWorkoutPriority()
                            }
                        }

                    }
            }else{
                callWorkoutPriority()
            }

        }
    }

    lateinit var workoutPriority : Array<WorkoutPriority>
    private fun callWorkoutPriority() {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val bodyType = document.data?.get("bodyType").toString()
                    val upperBodyScore = document.data?.get("upperScore").toString().toDouble()
                    val coreScore = document.data?.get("coreScore").toString().toDouble()
                    val lowerBodyScore = document.data?.get("lowerScore").toString().toDouble()
                    Log.d("TEST_DB CALL", "${RecEngine.getWorkoutPriorities(this, bodyType, upperBodyScore, lowerBodyScore, coreScore)}")
                    workoutPriority = RecEngine.getWorkoutPriorities(this, bodyType, upperBodyScore, lowerBodyScore, coreScore)
                    Log.d(TAG, "workout Prioriy: ${workoutPriority.toString()}")
                    getLastTwoWorkouts();
                }
            }
    }

    lateinit var lastTwoWorkouts : Array<String>
    private fun getLastTwoWorkouts() {
        FirebaseFirestore
            .getInstance()
            .collection("Users")
            .document(Constants?.CURRENT_FIREBASE_USER!!.uid)
            .collection("WorkoutSession")
            .orderBy("dateCompleted", Query.Direction.DESCENDING).limit(2)
            .get()
            .addOnCompleteListener {

                if (it.result?.documents?.size == 0){
                    lastTwoWorkouts = arrayOf("", "")
                }else {
                    if (it.result?.documents == null) {
                        lastTwoWorkouts = arrayOf("", "")
                    } else {
                        val workout1 = (it.result?.documents?.get(0)
                            ?.get("workout") as Map<String, Any?>)["name"] as String?
                        val workout2 = (it.result?.documents?.get(1)
                            ?.get("workout") as Map<String, Any?>)["name"] as String?

                        if (workout1 == null && workout2 == null) {
                            lastTwoWorkouts = arrayOf("", "")
                        }
                        if (workout2 == null) {
                            lastTwoWorkouts = arrayOf(workout1!!, "")
                        } else {
                            lastTwoWorkouts = arrayOf(workout1!!, workout2!!)
                            Log.d(
                                TAG,
                                "lastTwoWorkouts: ${lastTwoWorkouts.get(0)}, ${lastTwoWorkouts.get(1)}"
                            )
                        }
                    }
                }
                launchStandardWorkout()
            }
    }

    private fun launchStandardWorkout() {
        Log.d(TAG, RecEngine.recommendWorkoutCategory(workoutPriority, lastTwoWorkouts))
        val tempWorkout: Workout = Workout()
        tempWorkout.name = RecEngine.recommendWorkoutCategory(workoutPriority, lastTwoWorkouts)
        WebsiteSession(this, StartStandardWorkoutActivity::class.java, tempWorkout)
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
