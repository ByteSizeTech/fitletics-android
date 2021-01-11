package com.example.fitletics.models.support

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Workout : Serializable {

    var id: String? = null //TODO: @Vishal remove this if never used
    var name: String? = null
    var exerciseList: ArrayList<Exercise>? = null
    var difficulty: String? = null
    var time: String? = null
    var level: String? = null


    constructor(name: String?, exerciseList: ArrayList<Exercise> = ArrayList(), difficulty: String?, time: String?) {
        this.name = name
        this.exerciseList = exerciseList
        this.difficulty = difficulty
        this.time = time
    }

    constructor(){}

    constructor(workout: Workout?){
        if (workout == null){
            this.name = null
            this.exerciseList = null
            this.difficulty = null
            this.time = null
        } else {
            this.name = workout.name
            this.exerciseList = workout.exerciseList
            this.difficulty = workout.difficulty
            this.time = workout.time
        }
    }

    constructor(id: String?, name: String?, exerciseList: ArrayList<Exercise> = ArrayList(), difficulty: String?, time: String?) {
        this.id = id
        this.name = name
        this.exerciseList = exerciseList
        this.difficulty = difficulty
        this.time = time
    }

    constructor(name: String?, exerciseList: ArrayList<Exercise>?) {
        this.name = name
        this.exerciseList = exerciseList
    }

    constructor(workoutObject: Map<String, Any?>){
        this.name = workoutObject["name"].toString()
        this.time = workoutObject["time"].toString()
        this.level = workoutObject["level"].toString()
        this.difficulty = workoutObject["difficulty"].toString()
        this.exerciseList = makeArrayListExercisesFromMap(workoutObject["exerciseList"] as Map<String, Any?>)
    }

    fun makeArrayListExercisesFromMap(exerciseObject: Map<String, Any?>) : ArrayList<Exercise>?{
        val tempMuscleArray = ArrayList<Exercise>()

        for (exerciseInterator in exerciseObject){
            val exercise = Exercise(exerciseInterator.value as Map<String, Any?>)
            tempMuscleArray.add(exercise)
        }
        return tempMuscleArray
    }

    fun calculateDifficulty(){ //MAKE SURE EXERCISE LIST IS NOT NULL BEFORE CALLING THIS
        var difficulties: Float = 0.0.toFloat()
        var numOfExercises: Float = exerciseList!!.size.toFloat()
        for (exercise in exerciseList!!){

            //0-3 -> Easy, 3-7 -> Medium, 7-10 -> Hard
            if (exercise.difficulty!!.toLowerCase() == "Easy".toLowerCase()) difficulties += 2
            else if (exercise.difficulty!!.toLowerCase() == "Medium"!!.toLowerCase()) difficulties += 5.5.toFloat()
            else difficulties += 9
        }

        var difficultyNumeric: Float = difficulties/numOfExercises
        if (difficultyNumeric in 0.0..3.0) difficulty = "Easy"
        else if (difficultyNumeric > 3 && difficultyNumeric <= 7) difficulty = "Medium"
        else difficulty = "Hard"
    }

    fun calculateDuration(){
        var duration:Double = 0.0
        for (exercise in exerciseList!!){
            if (exercise.unit == Exercise.Unit.SECS) duration+=exercise.value!!.toDouble()
            else duration+=(exercise.value!!*exercise.timePerRep!!).toDouble()
        }
        var minutes:Int = (duration/(60.toDouble())).toInt()
        var seconds:Int = (duration%(60.toDouble())).toInt()

        time = minutes.toString() + "m " + seconds.toString() + "s"
    }

    fun firebaseFriendlyWorkout(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "name" to this.name,
            "difficulty" to this.difficulty,
            "time" to this.time,
            "exlist" to this.firebaseFriendlyExerciseList(),
            "exlist_size" to this.exerciseList?.size,
            "level" to this.level
        )

//        return mapOf<String, Any?>("null" to "null")
    }

    fun firebaseFriendlyExerciseList(): Map<String, Any?> {
        val tempMap: HashMap<String, Any?> = HashMap()
        for (exNum in 0 until this.exerciseList!!.size){
            tempMap["ex${exNum}"] = this.exerciseList!![exNum].firebaseFriendlyExercise()
        }
        return tempMap
    }
}