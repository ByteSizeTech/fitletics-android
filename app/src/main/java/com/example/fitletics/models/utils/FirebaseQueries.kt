package com.example.fitletics.models.utils

import com.example.fitletics.models.support.Constants
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseQueries {

    companion object {
        fun getUserInfo(): Map<String, Any> {

            var bodyType: String = ""
            var upperScore: Double = 0.0
            var coreScore: Double = 0.0
            var lowerScore: Double = 0.0
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        bodyType = document.data?.get("bodyType").toString()
                        upperScore = document.data?.get("upperScore").toString().toDouble()
                        coreScore = document.data?.get("coreScore").toString().toDouble()
                        lowerScore = document.data?.get("lowerScore").toString().toDouble()
                    }
                }


            return mapOf<String, Any>(
                "bodyType" to bodyType,
                "upper" to upperScore,
                "core" to coreScore,
                "lower" to lowerScore
            )
        }
    }

}