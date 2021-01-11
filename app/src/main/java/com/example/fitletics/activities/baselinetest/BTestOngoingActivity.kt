package com.example.fitletics.activities.baselinetest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.fitletics.R
import com.example.fitletics.activities.web.sessions.ActiveSessionActivity
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Workout
import com.example.fitletics.models.utils.WebsiteSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_baseline_test_ongoing.*

class BTestOngoingActivity : AppCompatActivity() {

    private val TAG = "BTest Ongoing"

    private var workoutObject: Workout? = null

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baseline_test_ongoing)

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the progress bar (for now)

        btest_ongoing_pb.setOnClickListener()
        {
            val intent = Intent(this, BTestCompleteActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/
        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)


        startBLT_button.isEnabled = false

        startBLT_button.setOnClickListener {
            startActiveSessionActivity()
        }

        workoutObject = intent.getSerializableExtra("Workout_object") as Workout?
        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        checkIfSessionIsComplete()
    }

    private fun checkIfSessionIsComplete() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .get()
            .addOnCompleteListener {doc->
                if (doc.result?.get("active_session") == "DB" && doc.result?.get("task_state") == "complete"){
                    startActivity(Intent(this, BTestCompleteActivity::class.java))
                    finish()
                }else{
                    startSession()
                }
            }
    }

    lateinit var sessionDescriptionTracker: ListenerRegistration
    private fun startSession() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(
                mapOf(
                    "uid" to Constants.CURRENT_FIREBASE_USER!!.uid,
                    "active_task" to "BLT",
                    "task_state" to "requested",
                    "task_message" to
                            mapOf(
                                "workout_obj" to workoutObject?.firebaseFriendlyWorkout(),
                                "active_session_listeners" to
                                        mapOf(
                                            "curr_ex_name" to "null",
                                            "curr_ex_unit" to "null",
                                            "curr_ex_progress" to "null",
                                            "curr_ex_goal" to "null",
                                            "skip_request" to "null"
                                        ))), SetOptions.merge()
            ).addOnSuccessListener {
                sessionDescriptionTracker =
                    FirebaseFirestore.getInstance()
                        .collection("Sessions")
                        .document(sessionUID!!)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null){
                                Log.d(TAG, "Listen failed.", e)
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                if (snapshot.data?.get("task_state") == "waiting") {
                                    Log.d(TAG, "Begin workout button enabled!")
                                    startBLT_button.isEnabled = true
                                }
                                if (snapshot.data?.get("task_state") == "cancelled") {
                                    sessionCancelled()
                                }
                                if (snapshot.data?.get("active_task") == "AS"  && snapshot.data?.get("task_state") == "requested") {
                                    startActiveSessionActivity()
                                }
                            }
                            else{
                                Log.d(TAG, "Session UID obj is null!")
                            }
                        }
            }
                }

    private fun startActiveSessionActivity() {
        sessionDescriptionTracker.remove()
        WebsiteSession(
            this,
            ActiveSessionActivity::class.java,
            workoutObject
        );
    }

    private fun sessionCancelled() {
        sessionDescriptionTracker.remove()
        finish()
    }
}

