package com.example.fitletics.models

import java.io.Serializable

class Exercise: Serializable {

    enum class Unit {
        TIME, REPS
    }

    //idea of a value is that you can just enter a value, say 20
    //and depending on the unit, it would be reps or a time value
    //this is done because some exercises (push ups, lunges) are done through reps,
    // while as others (planks) require the user to hold the pose for a set time
    var value: String ? = null
    var name: String? = null
    var unit: Unit? = null
    var difficulty: Int? = null
    var description: String? = null
    var targetMuscles: ArrayList<Muscle>? = null
    var harderExercise: Exercise? = null
    var easierExercise: Exercise? = null

    constructor(value: String?, name: String?) {
        this.value = value
        this.name = name
    }
}