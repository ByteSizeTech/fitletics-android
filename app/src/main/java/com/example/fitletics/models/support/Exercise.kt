package com.example.fitletics.models.support

import java.io.Serializable
import java.util.HashMap

class Exercise: Serializable, Comparable<Exercise>, Cloneable {

    enum class Unit {
        SECS, REPS
    }

    //idea of a value is that you can just enter a value, say 20
    //and depending on the unit, it would be reps or a time value
    //this is done because some exercises (push ups, lunges) are done through reps,
    // while as others (planks) require the user to hold the pose for a set time
    var id: String? = null
    var name: String? = null //
    var unit: Unit? = null  //
    var value: Int? = null  //
    var difficulty: String? = null  //
    var description: String? = null //
    var link: String? = null  //
    var targetMuscles: ArrayList<Muscle>? = null  //
    var harderExercise: Exercise? = null
    var easierExercise: Exercise? = null
    var timePerRep: Double? = null //applies to only rep-based exercises

    constructor(name: String?, value: Int?) {
        this.name = name
        this.value = value
    }

    constructor(ex: Exercise){
        this.name = ex.name
        this.unit = ex.unit
        this.value = ex.value
        this.difficulty = ex.difficulty
        this.link = ex.link
        this.harderExercise = null
        this.easierExercise = null
        for(muscle in ex.targetMuscles!!){
            this.targetMuscles?.add(Muscle(muscle))
        }
    }

    constructor(name: String, unit:Unit, value:Int, difficulty: String){
        this.name = name
        this.unit = unit
        this.value = value
        this.difficulty = difficulty
    }

    constructor(name: String?, unit: Unit?, value: Int?, difficulty: String?, timePerRep: Double?) {
        this.name = name
        this.unit = unit
        this.value = value
        this.difficulty = difficulty
        this.timePerRep = timePerRep
    }

    constructor()

    constructor(name: String, description: String, link: String, difficulty: String, unit: Unit?, targetMuscles: ArrayList<Muscle>?, timePerRep: Double?){
        this.name = name
        this.description = description
        this.link = link
        this.difficulty = difficulty
        this.unit = unit
        this.targetMuscles = targetMuscles
        this.timePerRep = timePerRep
    }

    constructor(name: String?, description: String?, link: String?, difficulty: String?, unit: Unit?){
        this.name = name
        this.description = description
        this.link = link
        this.difficulty = difficulty
        this.unit = unit
    }

    constructor(exerciseObject: Map<String, Any?>){
        this.name = exerciseObject["name"].toString()
        this.difficulty = exerciseObject["difficulty"].toString()
        this.link = exerciseObject["link"].toString()
        this.timePerRep = exerciseObject["timePerRep"].toString().toDoubleOrNull()
        if (exerciseObject["unit"].toString() == "REPS") {
            this.unit = Unit.REPS
        }else if (exerciseObject["unit"].toString() == "SECS"){
            this.unit = Unit.SECS
        }
        this.value = exerciseObject["value"].toString().toIntOrNull()
        this.targetMuscles = makeArrayListTargetMusclesFromMap(exerciseObject["targetMuscles"] as Map<String, Any?>)
    }

    fun makeArrayListTargetMusclesFromMap(muscleObject: Map<String, Any?>) : ArrayList<Muscle>?{
        val tempMuscleArray = ArrayList<Muscle>()

        for (muscleInterator in muscleObject){
            val muscle = Muscle(muscleInterator.value as Map<String, Any?>)
            tempMuscleArray.add(muscle)
        }
        return tempMuscleArray
    }

    override fun equals(other: Any?): Boolean {
        other as Exercise
        return if (this.name.equals(other.name)) {
            if (this.value == other.value) {
                this.unit == other.unit
            }else false
        }else false
    }

    override fun compareTo(other: Exercise): Int {
        return if (this.name.equals(other.name)) {
            if (this.value == other.value) {
                if (this.unit == other.unit) {
                    0
                }
                else 1
            }else 1
        }else 1
    }


    override fun toString(): String {
        return "$name, $unit, $value, $difficulty, $description, $link"
    }

    fun firebaseFriendlyExercise(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "value" to this.value,
            "name" to this.name,
            "unit" to this.unit.toString(),
            "difficulty" to this.difficulty,
            "description" to this.description,
            "link" to this.link,
            "targetMuscles" to firebaseFriendlyMuscleList(),
            "timePerRep" to this.timePerRep
        )
    }

    fun firebaseFriendlyMuscleList(): Map<String, Any?> {
        val tempMap: HashMap<String, Any?> = HashMap()
        for (exNum in 0 until this.targetMuscles!!.size){
            tempMap["m${exNum}"] = this.targetMuscles!![exNum].firebaseFriendlyMuscle()
        }
        return tempMap
    }

}