package com.example.fitletics.models

import java.io.Serializable

data class Workout(
    var id: String? = null,
    var name: String? = null,
    var exerciseList: ArrayList<Exercise>? = null,
    var difficulty: String? = null,
    var time: String? = null
) : Serializable {

    constructor(name: String?, exerciseList: ArrayList<Exercise> = ArrayList(), difficulty: String?, time: String?) : this() {
        this.name = name
        this.exerciseList = exerciseList
        this.difficulty = difficulty
        this.time = time
    }

    init{
        this.id = id
        this.name = name
        this.exerciseList = exerciseList
        this.difficulty = difficulty
        this.time = time
    }

    fun getDBFriendlyResult(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "name" to this.name,
            "difficulty" to this.difficulty,
            "time" to this.time,
            "exlist" to this.getExerciseMap(),
            "exlist_size" to this.exerciseList?.size
        )
    }

    fun getExerciseMap(): Map<String, Any?> {
        val tempMap: HashMap<String, Any?> = HashMap()
        for (exNum in 0 until this.exerciseList!!.size){
            tempMap["ex${exNum}"] = this.exerciseList!![exNum].getDBFriendlyResult()
        }
        return tempMap
    }

}