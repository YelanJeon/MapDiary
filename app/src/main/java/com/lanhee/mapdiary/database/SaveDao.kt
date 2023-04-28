package com.lanhee.mapdiary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lanhee.mapdiary.data.ActivitiesData

@Dao
interface SaveDao {
    @Query("SELECT * FROM activities WHERE active_date= :dateText")
    fun getAll(dateText: String): List<ActivitiesData>

    @Insert
    fun insert(data: ActivitiesData)

    @Update
    fun update(data: ActivitiesData)

    @Delete
    fun delete(data:ActivitiesData)
}