package com.example.fitletics.models

import java.util.*

class User {

    enum class BodyType {
        ENDOMORPHIC, ECTOMORPHIC, MESOMORPHIC
    }

    var name: String? = null
    var email: String? = null
    var DOB: String? = null
    var gender: String? = null
    var xp: Int? = null
    var weight: Int? = null
    var height: Int? = null
    var bodyType: BodyType? = null

    constructor(){
        name = "NO NAME"
        email = "NO EMAIL"
        DOB = "NO DOB"
        gender = "NO GENDER"
        weight = -1
        height = -1
    }

    constructor(
        name: String?,
        email: String?,
        DOB: String?,
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