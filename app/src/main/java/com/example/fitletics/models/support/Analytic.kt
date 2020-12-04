package com.example.fitletics.models.support

class Analytic {
    var date: String? = null
    var time: String? = null
    var cals: String? = null
    var value: String? = null

    constructor(date: String?, time: String?, calories: String?, value: String?) {
        this.date = date
        this.time = time
        this.cals = calories
        this.value = value
    }

    constructor() {
        this.date = "NO DATE"
        this.time = "NO TIME"
        this.cals = "NO CALS"
        this.value = "NO VALS"
    }

}