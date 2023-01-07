package com.alexsh3v.quicktask

import android.graphics.drawable.Drawable

class TextRow(var s: String, var drawableResource: Int, var colorResource: Int): Row() {
    var extraGap: Boolean = false
    fun showExtraGap(): Row {
        extraGap = true
        return this
    }
}
