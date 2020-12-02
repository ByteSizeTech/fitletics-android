package com.example.fitletics.models

import java.io.Serializable

class Muscle : Serializable {

    var name: String? = null
    var maleIntensity: Int? = null
    var femaleIntensity: Int? = null

    constructor(name: String?, maleIntensity: Int?, femaleIntensity: Int?){
        this.name = name
        this.maleIntensity = maleIntensity
        this.femaleIntensity = femaleIntensity
    }

    fun getDBFriendlyResult(){

    }
}