package com.example.fitletics.models.support

class ExerciseStat {

    var name : String? = null
    var timeTaken: Float? = null
    var repsDone: Int? = null
    var feedback: String? = null

    constructor(name: String, timeTaken: Float, repsDone: Int, feedback: String){
        this.name = name
        this.timeTaken = timeTaken
        this.repsDone = repsDone
        this.feedback = feedback
    }

    constructor(name: String, timeTaken: Float, repsDone: Integer, feedback: String){
        this.name = name
        this.timeTaken = timeTaken
        this.repsDone = repsDone.toInt()
        this.feedback = feedback
    }
}