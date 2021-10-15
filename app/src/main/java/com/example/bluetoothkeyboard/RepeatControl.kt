package com.example.bluetoothkeyboard

import android.view.View

class RepeatControl {
    companion object {
        var repeat = false
        val mutableMap : MutableMap<View,Boolean> = mutableMapOf()
    }
}