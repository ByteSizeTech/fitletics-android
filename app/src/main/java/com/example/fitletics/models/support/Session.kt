package com.example.fitletics.models.support

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Session {

    var workout: Workout? = null
    var timeTaken: Float? = null
    var dateCompleted: Date? = null //dd/mm/yy
    var caloriesBurned: Float? = null //
    //it was decided that each index of the hasmap would correspond to the completed stat
    var muscleGroupExersion: HashMap<String, Int>? = null
    var CompletedStats: ArrayList<ExerciseStat>? = null


    constructor(caloriesBurned: Float?, timeTaken: Float?, workout: Workout, CompletedStats: Map<String, Any?>){
        this.caloriesBurned = caloriesBurned
        this.timeTaken = timeTaken
        this.workout = workout
        this.CompletedStats = makeArrayListCompletedStatsFromMap(CompletedStats)
    }

    private fun makeArrayListCompletedStatsFromMap(completedStatsObject: Map<String, Any?>): ArrayList<ExerciseStat>? {
        val completedStatArray = ArrayList<ExerciseStat>()

        for (completedStatInterator in completedStatsObject){
            val completedStat = ExerciseStat(completedStatInterator.value as Map<String, Any?>)
            completedStatArray.add(completedStat)
        }
        return completedStatArray
    }

    constructor(
        workout: Workout,
        timeTaken: Float?,
        dateCompleted: Date?,
        caloriesBurned: Float?,
        muscleGroupExersion: HashMap<String, Int>?,
        CompletedStats: ArrayList<ExerciseStat>?
    ) {
        this.workout = workout
        this.timeTaken = timeTaken
        this.dateCompleted = dateCompleted
        this.caloriesBurned = caloriesBurned
        this.muscleGroupExersion = muscleGroupExersion
        this.CompletedStats = CompletedStats
    }

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(sessionMap: Map<String, Any?>){
        this.caloriesBurned = sessionMap["caloriesBurned"].toString().toFloatOrNull()
        this.timeTaken = sessionMap["timeTaken"].toString().toFloatOrNull()
        this.workout =  Workout(sessionMap["workout"] as Map<String, Any?>)
    }

    constructor(
        workout: Workout,
        timeTaken: Float?,
        dateCompleted: Date?,
        caloriesBurned: Float?,
        CompletedStats: ArrayList<ExerciseStat>?
    ) {
        this.workout = workout
        this.timeTaken = timeTaken
        this.dateCompleted = dateCompleted
        this.caloriesBurned = caloriesBurned
        this.CompletedStats = CompletedStats
        this.muscleGroupExersion = null
    }

    constructor(session: Session?){
        if (session == null){
            this.workout = null
            this.timeTaken = null
            this.dateCompleted = null
            this.caloriesBurned = null
            this.muscleGroupExersion = null
            this.CompletedStats = null
        }
        else {
            this.workout = session.workout
            this.timeTaken = session.timeTaken
            this.dateCompleted = session.dateCompleted
            this.caloriesBurned = session.caloriesBurned
            this.CompletedStats = session.CompletedStats
            this.muscleGroupExersion = null
        }
    }

    constructor()
}