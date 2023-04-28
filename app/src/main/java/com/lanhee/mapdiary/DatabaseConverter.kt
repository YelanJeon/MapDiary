package com.lanhee.mapdiary

import androidx.room.TypeConverter
import com.lanhee.mapdiary.utils.Utils
import java.util.Date

class DatabaseConverter {
    @TypeConverter
    fun dateToText(date: Date): String  = Utils.formatDateForDatabase(date)

    @TypeConverter
    fun textToDate(text: String) : Date = Utils.parseDateFromText(text)
}