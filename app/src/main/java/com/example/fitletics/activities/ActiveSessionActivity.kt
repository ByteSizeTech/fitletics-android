package com.example.fitletics.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fitletics.R
import com.example.fitletics.models.support.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_active_session.*
import kotlin.math.roundToInt

class ActiveSessionActivity : AppCompatActivity() {

    private val TAG  =  "ACTIVE_SESH"

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    private var workoutObject: Workout? = null

    var handler = Handler()
    var progress = 0.0
    var secsgoal = 0.0
    var canRunRunnable = false
    val runnable = object: Runnable {
        override fun run() {
            if (progress < secsgoal) {
                Log.d(
                    "PROGRESS_BAR",
                    "progress var: $progress, goal var: $secsgoal, progress_bar prog: ${session_exercise_progress.progress} and prog_bar goal: ${session_exercise_progress.max}"
                )
                progress++
                session_exercise_progress.progress = progress.roundToInt()
                handler.postDelayed(this, 1000)
            }
            else{
                Log.d("PROGRESS_BAR", "Removed callback -> exceeded goal")
                handler.removeCallbacks(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_session)

        session_rep_ll.visibility = View.GONE
        session_progress_bar_ll.visibility = View.GONE



//        workoutObject = intent.getSerializableExtra("Workout_object") as Workout?
//        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)


//        session_current_exercise_name_tv.setOnClickListener {
//            exUnitIsRep = !exUnitIsRep
//            if (exUnitIsRep){
//                exerciseProgressLinearLayout.removeAllViewsInLayout()
//                layoutInflater.inflate(R.layout.rep_count_layout, exerciseProgressLinearLayout)
//            }
//            else{
//                exerciseProgressLinearLayout.removeAllViewsInLayout()
//                layoutInflater.inflate(R.layout.exercise_secs_progress_bar_layout, exerciseProgressLinearLayout)
//            }
//        }

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

    lateinit var rootView: ViewGroup
    lateinit var repView: View
    lateinit var secView: View
    private lateinit var sessionTracker: ListenerRegistration
    //TODO: Move to top
    private fun startSession() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(mapOf(
                    "active_task" to "AS",
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
                                for (value in snapshot?.data?.entries!!){
                                    if (value.key == "task_message") {

                                        val workoutObjRef = (value.value as Map<String, Any>)["workout_obj"] as Map<String, String>
                                        session_workout_name_text_view.text = workoutObjRef["name"].toString()


                                        val listenerObjects = (value.value as Map<String, Any>)["active_session_listeners"] as Map<String, String>

                                        //Log.d(TAG, "${i++}: ${listenerO}: ${listener.value} \n")
                                        session_current_exercise_name_tv.text = listenerObjects["curr_ex_name"].toString()

//                                        rootView = this.findViewById(R.id.exercise_progress_linear_layout)


                                        if (listenerObjects["curr_ex_unit"].toString() == "REPS"){
                                            if (repInitialSetup) {
                                                session_progress_bar_ll.visibility = View.GONE
//                                                if (!rootView.children.toList().isEmpty()){
//                                                    rootView.removeAllViewsInLayout()
//                                                }
                                                canRunRunnable = false
                                                repInitialSetup = false
                                                secsInitialSetup = true // resets the initial setup variable when the unit is changed to a rep so that it can be called again when it changes to secs
//

                                                session_rep_ll.visibility = View.VISIBLE
//                                                val inflater = LayoutInflater.from(this)
//                                                repView = inflater.inflate(R.layout.rep_count_layout, rootView, true)
//                                                rootView.addView(reps)


                                            }

                                            session_total_rep_tv.text = listenerObjects["curr_ex_goal"].toString()
                                            Log.d("PROGRESS_BAR", "ex goal listener: ${listenerObjects["curr_ex_goal"].toString()}")


                                            session_current_rep_tv.text = listenerObjects["curr_ex_progress"].toString()
                                            Log.d("PROGRESS_BAR", "ex curr_rep listener: ${listenerObjects["curr_ex_progress"].toString()}")
                                        }
                                        else if (listenerObjects["curr_ex_unit"].toString() == "SECS"){
                                            if (secsInitialSetup) {
                                                session_rep_ll.visibility = View.GONE

//                                                if (!rootView.children.toList().isEmpty()) {
//                                                    Log.d("PROGRESS_BAR", "Tried to remove rep fom secs")
//                                                    rootView.()
//                                                }
                                                secsInitialSetup = false
                                                repInitialSetup = true

                                                session_progress_bar_ll.visibility = View.VISIBLE

//                                                val inflater = LayoutInflater.from(this)
//                                                secView = inflater.inflate(R.layout.exercise_secs_progress_bar_layout, rootView, true)

//                                                var exerciseProgressLinearLayout = findViewById<LinearLayout>(R.id.exercise_progress_linear_layout)
//                                                exerciseProgressLinearLayout.removeAllViewsInLayout()
//                                                layoutInflater.inflate(R.layout.exercise_secs_progress_bar_layout, exerciseProgressLinearLayout)
                                                session_exercise_progress.max = listenerObjects["curr_ex_goal"].toString().toInt()

                                                progress = 0.0
                                                secsgoal = listenerObjects["curr_ex_goal"].toString().toDouble()
                                                session_exercise_progress.progress = progress.roundToInt()

                                                Log.d("PROGRESS_BAR", "Pbar initialized with goal: " +
                                                        "${listenerObjects["curr_ex_goal"].toString().toInt()} and " +
                                                        "pvar val: ${progress} and " +
                                                        "pbar val: ${session_exercise_progress.progress}")

                                            }


                                            if(listenerObjects["curr_ex_progress"].toString() == "inpose") {
                                                runnable.run()
                                            }
                                            else{
                                                Log.d("PROGRESS_BAR", "Removed callback -> not inpose")
                                                handler.removeCallbacks(runnable)
                                            }
                                        }
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