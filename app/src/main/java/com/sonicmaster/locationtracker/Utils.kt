package com.sonicmaster.locationtracker

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast


const val FILE_NAME = "Logs.txt"

object SharedPrefs {

    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "params"
    const val NAME = "name"
    const val MOBILE = "mobile"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun read(key: String): String? {
        return prefs.getString(key, null)
    }


    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }
}

fun View.toast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}
