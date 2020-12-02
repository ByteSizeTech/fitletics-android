package com.example.fitletics.models

import java.io.Serializable

class Exercise: Serializable {

    enum class Unit {
        SECS, REPS
    }

    //idea of a value is that you can just enter a value, say 20
    //and depending on the unit, it would be reps or a time value
    //this is done because some exercises (push ups, lunges) are done through reps,
    // while as others (planks) require the user to hold the pose for a set time
    var value: String ? = null
    var name: String? = null
    var unit: Unit? = null
    var difficulty: String? = null
    var description: String? = null
    var link: String? = null
    var targetMuscles: ArrayList<Muscle>? = null
    var harderExercise: Exercise? = null
    var easierExercise: Exercise? = null

    constructor(value: String?, name: String?) {
        this.value = value
        this.name = name
    }

    constructor(name: String?, description: String?, link: String?, difficulty: String?, unit: Unit?){
        this.name = name
        this.description = description
        this.link = link
        this.difficulty = difficulty
        this.unit = unit
    }

    fun getDBFriendlyResult(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "value" to this.value,
            "name" to this.name,
            "unit" to this.unit.toString(),
            "difficulty" to this.difficulty,
            "description" to this.description,
            "link" to this.link
        )
    }

}