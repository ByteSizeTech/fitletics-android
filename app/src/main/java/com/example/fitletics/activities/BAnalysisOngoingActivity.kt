package com.example.fitletics.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.models.support.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_body_analysis_ongoing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BAnalysisOngoingActivity : AppCompatActivity() {

//    var intentUID: String? = null
    var sessionUID: String? = null

    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_analysis_ongoing)

        captureBA_button.isEnabled = false

        captureBA_button.setOnClickListener {
            sendCaptureRequest()
        }

        cancelBA_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sendCancelRequest()
            }
            bodyAnalysisCancelled()
        }


        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        banalysis_ongoing_pb.setOnClickListener()
        {
            val intent = Intent(this, BAnalysisCompleteActivity::class.java)
            startActivity(intent)
        }

        /*===============================================*/



        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        startSession()
    }

    lateinit var baSessionListener: ListenerRegistration
    private fun startSession() {
        //check if session is already populated
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .update(mapOf(
                "uid" to Constants.CURRENT_FIREBASE_USER!!.uid,
                "active_task" to "BA",
                "task_state" to "requested"))
            .addOnSuccessListener {
                baSessionListener =
                FirebaseFirestore.getInstance()
                    .collection("Sessions")
                    .document(sessionUID!!)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null){
                            Log.d("BA_SESSION", "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.data?.get("task_state") == "ongoing"){
                                Log.d("BA_SESSION", "Capture button enabled!")
                                captureBA_button.isEnabled = true
                            }
                            if (snapshot.data?.get("task_state") == "complete"){
                                Log.d("BA_SESSION", "Going to body Analysis complete!")
                                bodyAnalysisComplete()
                            }
//                            if (snapshot.data?.get("task_state") == "cancelled"){
//                                bodyAnalysisCancelled()
//                                return@addSnapshotListener
//                            }
                        }
                        else{
                            Log.d("BA_SESSION", "Session UID obj is null!")
                        }
                    }

            }.addOnFailureListener { e ->
                Log.d("BA_SESSION", "Error updating document")
                startActivity(Intent(this, ConnectPCQRActivity::class.java));
                Toast.makeText(baseContext, "Error! Scan QR", Toast.LENGTH_SHORT).show()

                //TODO: delete share pref if exists
            }
    }


    private fun sendCaptureRequest() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .update(mapOf("task_message" to "capture"))
            .addOnSuccessListener {
                Log.d("BA_SESSION", "Sent capture message to session")
            }.addOnFailureListener {
                Log.d("BA_SESSION", "Error sending capture message")
            }
    }

    private fun sendCancelRequest() {
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .update(mapOf("task_message" to "cancelled"))
            .addOnSuccessListener {
                Log.d("BA_SESSION", "Sent cancel message to session")
            }.addOnFailureListener {
                Log.d("BA_SESSION", "Error sending cancel message")
            }
    }

    private fun bodyAnalysisComplete() {
        baSessionListener.remove()
        Log.d("BA_SESSION", "Starting complete activity")
        startActivity(Intent(this, BAnalysisCompleteActivity::class.java))
        finish()
    }

    private fun bodyAnalysisCancelled() {
        baSessionListener.remove()
        startActivity(Intent(this, MainActivity::class.java))
    }
}