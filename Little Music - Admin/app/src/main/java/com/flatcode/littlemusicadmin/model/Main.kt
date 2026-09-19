package com.flatcode.littlemusicadmin.model

import android.content.Context

class Main {
    var title: String? = null
    var image = 0
    var number = 0
    var navigate: ((Context) -> Unit)? = null

    constructor()

    constructor(image: Int, title: String?, number: Int, navigate: ((Context) -> Unit)?) {
        this.image = image
        this.number = number
        this.title = title
        this.navigate = navigate
    }
}
