package com.example.fitletics.models

import java.util.*

class User {

    enum class BodyType {
        ENDOMORPHIC, ECTOMORPHIC, MESOMORPHIC
    }

    var name: String? = null
    var email: String? = null
    var DOB: Date? = null
    var gender: String? = null
    var xp: Int? = null
    var weight: Int? = null
    var height: Int? = null
    var bodyType: BodyType? = null


    constructor(
        name: String?,
        email: String?,
        DOB: Date?,
        gender: String?,
        xp: Int?,
        weight: Int?,
        height: Int?,
        bodyType: BodyType?
    ) {
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.xp = xp
        this.weight = weight
        this.height = height
        this.bodyType = bodyType
    }
}