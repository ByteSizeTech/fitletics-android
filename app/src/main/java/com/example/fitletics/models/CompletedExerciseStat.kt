package com.example.fitletics.models

import java.util.*

class CompletedExerciseStat {

    var exercise: Exercise? = null
    var timeTaken: Date? = null
    var feedback: String? = null

    constructor(exercise: Exercise?, timeTaken: Date?, feedback: String?) {
        this.exercise = exercise
        this.timeTaken = timeTaken
        this.feedback = feedback
    }
}