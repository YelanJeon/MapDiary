package com.lanhee.mapdiary.data.database

import androidx.room.TypeConverter
import com.lanhee.mapdiary.utils.DateFormatter
import java.util.Date

class DatabaseConverter {
    @TypeConverter
    fun dateToText(date: Date): String  = DateFormatter.formatDateForDatabase(date)

    @TypeConverter
    fun textToDate(text: String) : Date = DateFormatter.parseDateFromText(text)
}