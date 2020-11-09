package com.example.fitletics.models

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Session {

    var workout: Workout? = null
    var timeTaken: Float? = null
    var dateCompleted: Date? = null
    var caloriesBurned: Float? = null
    var muscleGroupExersion: HashMap<String, Int>? = null
    var CompletedStats: ArrayList<ExerciseStat>? = null

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
}