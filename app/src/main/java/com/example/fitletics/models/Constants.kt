package com.example.fitletics.models

import com.google.firebase.auth.FirebaseUser

class Constants {
    companion object {
        var CURRENT_USER: FirebaseUser? = null
    }

    constructor(user : FirebaseUser?){
        CURRENT_USER = user
    }
}