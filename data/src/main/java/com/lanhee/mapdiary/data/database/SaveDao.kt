package com.lanhee.mapdiary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lanhee.mapdiary.data.ActivitiesData

@Dao
interface SaveDao {
    @Query("SELECT * FROM activities WHERE active_date= :dateText ORDER BY my_order ASC")
    fun getAll(dateText: String): List<ActivitiesData>

    @Insert
    fun insert(data: ActivitiesData)

    @Update
    fun update(data: ActivitiesData)

    @Query("DELETE FROM activities WHERE idx = :idx")
    fun delete(idx:Int)
}