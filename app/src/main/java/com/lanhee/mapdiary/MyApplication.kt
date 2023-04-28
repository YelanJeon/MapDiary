package com.lanhee.mapdiary

import android.app.Application
import androidx.core.content.res.ResourcesCompat
import com.lanhee.mapdiary.database.AppDatabase

class MyApplication: Application() {
    companion object {
        lateinit var CLIENT_ID: String
        lateinit var CLIENT_SECRET: String
    }

    override fun onCreate() {
        super.onCreate()
        CLIENT_ID = resources.getString(R.string.MAP_CLIENT_ID)
        CLIENT_SECRET = resources.getString(R.string.MAP_CLIENT_SECRET)

        AppDatabase.init(applicationContext)
    }
}