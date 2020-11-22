package com.example.fitletics.models

import java.io.Serializable

class Workout : Serializable {

    var id: String? = null
    var name: String? = null
    var exerciseList: ArrayList<Exercise>? = null
    var difficulty: String? = null
    var time: String? = null



    constructor(name: String?, exerciseList: ArrayList<Exercise> = ArrayList(), difficulty: String?, time: String?) {
        this.name = name
        this.exerciseList = exerciseList
        this.difficulty = difficulty
        this.time = time
    }

    constructor(){

    }
}