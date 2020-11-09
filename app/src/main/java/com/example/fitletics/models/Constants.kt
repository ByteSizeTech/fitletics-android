package com.example.fitletics.models

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Constants {

    companion object {
        var CURRENT_FIREBASE_USER: FirebaseUser? = null
        var CURRENT_USER: User? = null
    }



//    constructor(user : FirebaseUser?){
//        CURRENT_FIREBASE_USER = user
//    }
}