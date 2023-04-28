package com.lanhee.mapdiary.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object {

        const val FORMATTER_DB = "yyyy-MM-dd"
        const val FORMATTER_KR = "yyyy년 MM월 dd일"

        fun dimensionToPixel(context: Context, dp: Float) : Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        }

        fun getScreenWidth(context: Context) : Int{
            return context.resources.displayMetrics.widthPixels
        }

        fun getScreenHeight(context: Context) : Int{
            return context.resources.displayMetrics.heightPixels
        }

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
}
