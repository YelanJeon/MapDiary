package com.lanhee.mapdiary.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object PrefModule {

    const val _PREF_NAME = "defaultSetting"
    const val _P_DETECT_LOCATION_AUTO = "_P_DETECT_LOCATION_AUTO"

    private lateinit var pref: SharedPreferences

    fun init(context: Context) {
        pref = context.getSharedPreferences(_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setDetectLocationAuto(setAuto: Boolean) {
        pref.edit().putBoolean(_P_DETECT_LOCATION_AUTO, setAuto).apply()
    }

    fun isDetectLocationAuto(): Boolean {
        return pref.getBoolean(_P_DETECT_LOCATION_AUTO, false)
    }

}