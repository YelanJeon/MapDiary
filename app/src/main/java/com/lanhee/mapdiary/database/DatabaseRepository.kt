package com.lanhee.mapdiary.database

import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.utils.Utils
import kotlinx.coroutines.withContext
import java.util.Date

class DatabaseRepository(private val database: AppDatabase) {
    suspend fun load(date: Date): List<ActivitiesData>
    = withContext(database.ioDispatcher){
        database.saveDao().getAll(Utils.formatDateForDatabase(date))
    }

    suspend fun save(data: ActivitiesData)
    = withContext(database.ioDispatcher) {
        database.saveDao().insert(data)
    }

    suspend fun update(data: ActivitiesData)
    = withContext(database.ioDispatcher) {
        database.saveDao().update(data)
    }

    suspend fun delete(data: ActivitiesData)
    = withContext(database.ioDispatcher) {
        database.saveDao().delete(data)
    }
}