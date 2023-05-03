package com.lanhee.mapdiary.data.database

import com.lanhee.mapdiary.utils.DateFormatter
import com.lanhee.mapdiary.data.ActivitiesData
import kotlinx.coroutines.withContext
import java.util.Date

class DatabaseRepository(private val database: AppDatabase) {
    suspend fun load(date: Date): List<ActivitiesData>
    = withContext(database.ioDispatcher){
        database.saveDao().getAll(DateFormatter.formatDateForDatabase(date))
    }

    suspend fun save(data: ActivitiesData)
    = withContext(database.ioDispatcher) {
        database.saveDao().insert(data)
    }

    suspend fun update(data: ActivitiesData)
    = withContext(database.ioDispatcher) {
        database.saveDao().update(data)
    }

    suspend fun delete(idx: Int)
    = withContext(database.ioDispatcher) {
        database.saveDao().delete(idx)
    }

}