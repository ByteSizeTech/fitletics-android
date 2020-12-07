package com.example.fitletics.fragments.homepage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitletics.R
import com.example.fitletics.activities.BAnalysisOngoingActivity
import com.example.fitletics.activities.LoginActivity
import com.example.fitletics.models.utils.WebsiteSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.Exception


class SettingsFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var sessionId: String

    private val TAG = "LOGOUT_FRAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_settings, container, false)

        rootView.findViewById<Button>(R.id.logout_button).setOnClickListener()
        {
            try {
                logoutFromWebsite(this.activity!!.getSharedPreferences("session_uid_data", Context.MODE_PRIVATE));
            }
            catch (e: Exception){
                Log.d(TAG, "logout failed with exception: ${e.message}")
                auth.signOut()
                startActivity(Intent(this.activity!!, LoginActivity::class.java))
                this.activity!!.finish()
            }
        }

        rootView.findViewById<Button>(R.id.button3).setOnClickListener()
        {
            WebsiteSession(
                this.activity!!,
                BAnalysisOngoingActivity::class.java,
                null
            )
//            this.activity!!.finish()
        }

        return rootView
    }


    private fun logoutFromWebsite(sharedPreferences: SharedPreferences) {
        Log.d("LOGOUT_FRAG", "Logging out from websites")
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sharedPreferences.getString("UID", null)!!)
            .update(
                mapOf(
                "uid" to null,
                "active_task" to null,
                "task_state" to null,
                "task_message" to null)
            )
            .addOnCompleteListener {
                Log.d("LOGOUT_FRAG", "Sent logout command to: ${sharedPreferences.getString("UID", null)}")
                Log.d("LOGOUT_FRAG", "Calling delete pref func")
                auth.signOut()
                deleteSharedPref(this.activity!!.getSharedPreferences("session_uid_data", Context.MODE_PRIVATE));
            }
            .addOnFailureListener {e ->
                Log.d("LOGOUT_FRAG", "Logout func FAILED! w/ ${e.cause}")
//                auth.signOut()
//                startActivity(Intent(this.activity!!, LoginActivity::class.java))
//                this.activity!!.finish()
            }
    }

    private fun deleteSharedPref(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.apply {
            remove("UID")
        }.apply()
        Log.d("LOGOUT_FRAG", "Shared Pref ID removed: ${sharedPreferences.getString("UID", null)}")
        startActivity(Intent(this.activity!!, LoginActivity::class.java))
        this.activity!!.finish()
    }
}