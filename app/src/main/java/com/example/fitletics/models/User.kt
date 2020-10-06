package com.example.fitletics.models

import java.util.*

class User {

    enum class BodyType {
        ENDOMORPHIC, ECTOMORPHIC, MESOMORPHIC
    }

    var name: String? = null
    var email: String? = null
    var DOB: Calendar? = null
    var gender: String? = null
    var xp: Int? = null
    var weight: Int? = null
    var height: Int? = null
    var bodyType: BodyType? = null


    constructor(
        name: String?,
        email: String?,
        DOB: Calendar?,
        gender: String?,
        weight: Int?,
        height: Int?
    ) {
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.weight = weight
        this.height = height
    }
}