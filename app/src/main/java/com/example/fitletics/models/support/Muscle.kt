package com.example.fitletics.models.support

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

    constructor(muscle: Muscle){
        this.name = muscle.name
        this.maleIntensity = muscle.maleIntensity
        this.femaleIntensity = muscle.femaleIntensity
    }

    public fun firebaseFriendlyMuscle(): Map<String, Any?>{
        return mapOf<String, Any?>(
            "name" to this.name,
            "maleIntensity" to this.maleIntensity,
            "femaleIntensity" to this.femaleIntensity
        )

    }


}