package com.lanhee.mapdiary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lanhee.mapdiary.data.ActivitiesData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Database(entities = [ActivitiesData::class], version = 1)
@TypeConverters(DatabaseConverter::class)
abstract class AppDatabase(
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): RoomDatabase(){
    abstract fun saveDao(): SaveDao

    companion object {
        const val DATABASE  = "MapDiary"

        private var database: AppDatabase? = null
        fun getInstance(): AppDatabase {
            return database!!
        }

        fun init(context: Context) {
            synchronized(AppDatabase::class.java) {
                database = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DATABASE
                ).build()
            }
        }
    }

}