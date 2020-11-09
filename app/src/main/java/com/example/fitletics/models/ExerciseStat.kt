package com.example.fitletics.models

class ExerciseStat {

    var name : String? = null
    var timeTaken: Float? = null
    var repsDone: Integer? = null
    var feedback: String? = null

    constructor(name: String, timeTaken: Float, repsDone: Integer, feedback: String){
        this.name = name
        this.timeTaken = timeTaken
        this.repsDone = repsDone
        this.feedback = feedback
    }
}