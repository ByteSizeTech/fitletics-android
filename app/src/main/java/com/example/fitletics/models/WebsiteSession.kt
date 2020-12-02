package com.example.fitletics.models

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.fitletics.activities.ConnectPCQRActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_body_analysis_ongoing.*

class WebsiteSession(context: Context, intentClass: Class<*>, workout: Workout?) {
    private val TAG = "WEB_SESH"
    companion object {
        var intentUID: String? = null
        lateinit var sessionIntent: Intent
        lateinit var sharedPref: SharedPreferences

        fun setSharedPrefs(uid: String){
            val editor = sharedPref.edit()
            editor.apply{
                putString("UID", uid)
            }.apply()
            Log.d("WEB_SESH", "UID was saved to pref from intent from Camera as $uid")
            Constants.SESSION_CONNECTION = uid;
        }
    }

    init {
        sessionIntent = Intent(context, intentClass)
        var sessionUID: String? = null
        sharedPref = context.getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = populateUID()


        if (sessionUID.isNullOrBlank()){
            //change intent to connect to PC
            Log.d(TAG, "SessionUID was $sessionUID, Scan QR")
            val intent: Intent = Intent(context, ConnectPCQRActivity::class.java)
            intent.putExtra("final_intent", intentClass);
            intent.putExtra("Workout_object", workout)
            context.startActivity(intent);
        }
        else {  //everything is fine, start session
            Log.d(TAG, "Starting ${intentClass.simpleName} session w $sessionUID")
            val intent: Intent = Intent(context, intentClass)
            intent.putExtra("Workout_object", workout)
            context.startActivity(intent);
        }
    }

    private fun populateUID(): String? {
        var uid: String? = null
        val preferenceString = sharedPref.getString("UID", null)
        if (intentUID.isNullOrBlank()){
            Log.d(TAG, "intent UID is : $intentUID")
        }
        else{
            uid = intentUID!!;
            val editor = sharedPref.edit()
            editor.apply{
                putString("UID", uid)
            }.apply()
            Log.d(TAG, "UID was saved to pref from intent as $uid")
            Constants.SESSION_CONNECTION = uid;
            Log.d(TAG, "populateUID func returned UID from intent: $uid")
            return uid
        }

        if(preferenceString.isNullOrBlank()){
            Log.d(TAG, "preference UID is : $preferenceString")
        }
        else {
            uid = preferenceString
            Log.d(TAG, "populateUID func returned UID from prefs: $uid")
            return uid
        }

        Log.d(TAG, "populateUID func returned UID : $uid")
        return uid
    }





}