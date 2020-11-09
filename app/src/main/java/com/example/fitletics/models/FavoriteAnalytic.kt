package com.example.fitletics.models

class FavoriteAnalytic {

    var title: String? = null
    var stat: String? = null
    var color: Int? = null

    constructor(title: String?, stat: String?, color: Int){
        this.title = title
        this.stat = stat
        this.color = color
    }
}