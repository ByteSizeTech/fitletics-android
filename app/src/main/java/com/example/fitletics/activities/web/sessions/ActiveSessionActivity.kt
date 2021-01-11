package com.example.fitletics.activities.web.sessions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.fitletics.R
import com.example.fitletics.activities.web.connect.ConnectPCQRActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_active_session.*
import kotlin.math.roundToInt

class ActiveSessionActivity : AppCompatActivity() {

    private val TAG  =  "ACTIVE_SESH"
    //Initializing exercise recognition

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_session)

        session_rep_ll.visibility = View.GONE
        session_progress_bar_ll.visibility = View.GONE

        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        session_skip_button.setOnClickListener {
            requestSkip()
        }

        session_end_button.setOnClickListener {
            requestSessionEnd()
        }

        startSession()

    }

    private fun requestSessionEnd() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(mapOf(
                "task_state" to "endrequest"
            ), SetOptions.merge())
            .addOnCompleteListener {
                Log.d(TAG, "End requested")
                endActivity()
            }
    }

    private fun requestSkip() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(mapOf(
                "task_message" to mapOf(
                    "active_session_listeners" to mapOf(
                        "skip_request" to "requested"
                    )
                )
            ), SetOptions.merge())
            .addOnCompleteListener {
                Log.d(TAG, "Skip requested")
            }
    }

    private lateinit var sessionTracker: ListenerRegistration
    var progress = 0.0
    var secsgoal = 0.0
    var canRunRunnable = false
    private fun startSession() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(mapOf(
                    "task_state" to "ongoing"
                ), SetOptions.merge())
            .addOnSuccessListener {
//                sendWorkout();
                Log.d(TAG, "Session name changed to 1st exercise!")
                var repInitialSetup = true
                var secsInitialSetup = true
                sessionTracker =
                FirebaseFirestore.getInstance()
                    .collection("Sessions")
                    .document(sessionUID!!)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null){
                            Log.d(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.data?.get("task_state") == "ongoing") {
                                val taskMessage = snapshot?.data?.get("task_message") as Map<String, Any>
                                val workoutObjRef = (taskMessage["workout_obj"] as Map<String, Any>) as Map<String, String>
                                val listenerObjects = (taskMessage["active_session_listeners"] as Map<String, String>)

                                session_workout_name_text_view.text = workoutObjRef["name"].toString()

                                if(listenerObjects["curr_ex_unit"] == "REPS"){
                                session_current_exercise_name_tv.text = listenerObjects["curr_ex_name"].toString()
                                    if (repInitialSetup) {
                                        session_progress_bar_ll.visibility = View.GONE
                                        canRunRunnable = false
                                        repInitialSetup = false
                                        secsInitialSetup = true // resets the initial setup variable when the unit is changed to a rep so that it can be called again when it changes to secs

                                        session_rep_ll.visibility = View.VISIBLE
                                    }
                                    session_total_rep_tv.text = listenerObjects["curr_ex_goal"].toString()
                                    Log.d("PROGRESS_BAR", "ex goal listener: ${listenerObjects["curr_ex_goal"].toString()}")

                                    session_current_rep_tv.text = listenerObjects["curr_ex_progress"].toString()
                                    Log.d("PROGRESS_BAR", "ex curr_rep listener: ${listenerObjects["curr_ex_progress"].toString()}")

                                }else if (listenerObjects["curr_ex_unit"].toString() == "SECS"){
                                    session_current_exercise_name_tv.text = listenerObjects["curr_ex_name"].toString()
                                    if (secsInitialSetup) {
                                        session_rep_ll.visibility = View.GONE
                                        secsInitialSetup = false
                                        repInitialSetup = true

                                        session_progress_bar_ll.visibility = View.VISIBLE
                                        session_exercise_progress.max = listenerObjects["curr_ex_goal"].toString().toInt()

                                        progress = 0.0
                                        secsgoal = listenerObjects["curr_ex_goal"].toString().toDouble()
                                        session_exercise_progress.progress = progress.roundToInt()

                                        Log.d("PROGRESS_BAR", "Pbar initialized with goal: " +
                                                "${listenerObjects["curr_ex_goal"].toString().toInt()} and " +
                                                "pvar val: ${progress} and " +
                                                "pbar val: ${session_exercise_progress.progress}")
                                    }

                                    try {
                                        if (listenerObjects["curr_ex_progress"]!!.toString() != "null") {
                                            session_exercise_progress.progress =
                                                session_exercise_progress.max - (session_exercise_progress.max - listenerObjects["curr_ex_progress"]!!.toInt())
                                        }
                                    }catch (e: Exception){
                                        Log.d(TAG, "Error reading type")
                                    }
                                }
                            }
                            if (snapshot.data?.get("task_state") == "complete"){
                                endActivity()
                                return@addSnapshotListener
                            }
                        }
                        else{
                            Log.d(TAG, "Session UID obj is null!")
                        }
                    }
            }.addOnFailureListener { e ->
                Log.d(TAG, "Error updating document")
                startActivity(Intent(this, ConnectPCQRActivity::class.java));
                Toast.makeText(baseContext, "Error! Scan QR", Toast.LENGTH_SHORT).show()
            }
    }

    private fun endActivity() {
        sessionTracker.remove()
        finish()
    }

}