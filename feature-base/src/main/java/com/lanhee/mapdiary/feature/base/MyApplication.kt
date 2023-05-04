package com.lanhee.mapdiary.feature.base

import android.app.Application
import com.lanhee.mapdiary.data.database.AppDatabase

class MyApplication: Application() {
    companion object {
        lateinit var CLIENT_ID: String
        lateinit var CLIENT_SECRET: String
    }

    override fun onCreate() {
        super.onCreate()
        CLIENT_ID = resources.getString(R.string.MAP_CLIENT_ID)
        CLIENT_SECRET = resources.getString(R.string.MAP_CLIENT_SECRET)

        PrefModule.init(applicationContext)
        AppDatabase.init(applicationContext)
    }
}