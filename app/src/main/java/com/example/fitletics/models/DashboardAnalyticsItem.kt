package com.example.fitletics.models

import android.graphics.Color

class DashboardAnalyticsItem {

    var title: String ? = null
    var stat: String? = null
    var color: Int? = null

    constructor(title: String?, stat: String?, color: Int){
        this.title = title
        this.stat = stat
        this.color = color
    }
}