package com.example.fitletics.models.support

class User {

    enum class BodyType {
        ENDOMORPHIC, ECTOMORPHIC, MESOMORPHIC
    }

    var userID: String? = null
    var name: String? = null
    var email: String? = null
    var DOB: String? = null
    var gender: String? = null
    var xp: Int? = null
    var weight: Int? = null
    var height: Int? = null
    var bodyType: BodyType? = null
    var caloriesPerStep: Double? = null

    //TODO: Add these after BLT in signup Process
    var upperScore :Double? = null
    var coreScore: Double? = null
    var lowerScore: Double? = null

    constructor(){
        userID= "NO ID"
        name = "NO NAME"
        email = "NO EMAIL"
        DOB = "NO DOB"
        gender = "NO GENDER"
        weight = -1
        height = -1
    }

    constructor(
        userID: String?,
        name: String?,
        email: String?,
        DOB: String?,
        gender: String?,
        weight: Int?,
        height: Int?
    ) {
        this.userID = userID
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.weight = weight
        this.height = height
    }

    constructor(
        userID: String?,
        name: String?,
        email: String?,
        DOB: String?,
        gender: String?,
        weight: Int?,
        height: Int?,
        bodyType: String?
    ) {
        this.userID = userID
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.weight = weight
        this.height = height
        when(bodyType){
            "ENDOMORPHIC" -> this.bodyType =
                BodyType.ENDOMORPHIC
            "ECTOMORPHIC" -> this.bodyType =
                BodyType.ECTOMORPHIC
            "MESOMORPHIC" -> this.bodyType =
                BodyType.MESOMORPHIC
        }
    }

    constructor(
        userID: String?,
        name: String?,
        email: String?,
        DOB: String?,
        gender: String?,
        weight: Int?,
        height: Int?,
        bodyType: BodyType?,
        xp: Int?
    ) {
        this.userID = userID
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.weight = weight
        this.height = height
        this.bodyType = bodyType
        this.xp = xp
    }

    constructor(
        userID: String?,
        name: String?,
        email: String?,
        DOB: String?,
        gender: String?,
        weight: Int?,
        height: Int?,
        xp: Int?
    ) {
        this.userID = userID
        this.name = name
        this.email = email
        this.DOB = DOB
        this.gender = gender
        this.weight = weight
        this.height = height
        this.xp = xp
    }
}