package com.example.fitletics.models.support

import com.google.firebase.auth.FirebaseUser

class Constants {
    companion object {
        private val TAG = "CONSTANT_OBJ"
        var CURRENT_FIREBASE_USER: FirebaseUser? = null
        var CURRENT_USER: User? = null
        var SESSION_CONNECTION: String? = null
        var STEP_COUNT: String? = null
    }

}