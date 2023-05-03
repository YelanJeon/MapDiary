package com.lanhee.mapdiary.utils

import java.text.SimpleDateFormat
import java.util.Date

object DateFormatter {
    const val FORMATTER_DB = "yyyy-MM-dd"
    const val FORMATTER_KR = "yyyy년 MM월 dd일"

    fun formatDateForText(date: Date): String {
        return SimpleDateFormat(FORMATTER_KR).format(date)
    }

    fun formatDateForDatabase(date: Date): String {
        return SimpleDateFormat(FORMATTER_DB).format(date)
    }

    fun parseDateFromText(text: String): Date {
        return SimpleDateFormat(FORMATTER_DB).parse(text)
    }
}
