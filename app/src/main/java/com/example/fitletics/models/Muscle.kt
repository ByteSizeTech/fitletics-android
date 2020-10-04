package com.example.fitletics.models

class Muscle {

    var name: String? = null
    var maleIntensity: Int? = null
    var femaleIntensity: Int? = null

    constructor(name: String?, maleIntensity: Int?, femaleIntensity: Int?){
        this.name = name
        this.maleIntensity = maleIntensity
        this.femaleIntensity = femaleIntensity
    }
}