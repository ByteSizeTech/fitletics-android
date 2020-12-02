package com.example.fitletics.models

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.net.Socket

class Constants {
    companion object {
        private val TAG = "CONSTANT_OBJ"
        var CURRENT_FIREBASE_USER: FirebaseUser? = null
        var CURRENT_USER: User? = null
        var SESSION_CONNECTION: String? = null
        var STEP_COUNT: String? = null
    }



//    constructor(user : FirebaseUser?){
//        CURRENT_FIREBASE_USER = user
//    }
}