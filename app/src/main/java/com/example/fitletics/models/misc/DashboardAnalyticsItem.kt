package com.example.fitletics.models.misc

import android.graphics.Color
import com.example.fitletics.R

class DashboardAnalyticsItem {

    var title: String ? = null
    var stat: String? = null
    var color: Int? = null

    constructor(title: String?, stat: String?, color: Int){
        this.title = title
        this.stat = stat
        this.color = color
    }

    constructor(title: String?, stat: String?, index: Long){
        this.title = title
        this.stat = stat
        getColor(index)
    }

    fun getColor(num: Long){
        when (num){
            1.toLong() -> this.color = R.color.color1
            2.toLong() -> this.color = R.color.color2
            3.toLong() -> this.color = R.color.color3
            4.toLong() -> this.color = R.color.color4
            5.toLong() -> this.color = R.color.color5
            6.toLong() -> this.color = R.color.color6
            7.toLong() -> this.color = R.color.color7
            8.toLong() -> this.color = R.color.color8
            9.toLong() -> this.color = R.color.color9
        }
    }
}