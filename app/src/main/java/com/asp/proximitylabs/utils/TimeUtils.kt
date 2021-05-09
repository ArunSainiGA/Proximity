package com.asp.proximitylabs.utils

import android.text.format.DateUtils
import java.text.ParseException

object TimeUtils {
    fun getPrettyTime(timeInMilli: Long): String{
        return try {
            val now = System.currentTimeMillis()
            val ago = DateUtils.getRelativeTimeSpanString(timeInMilli, now, DateUtils.SECOND_IN_MILLIS)
            return ago.toString()
        } catch (e: ParseException) {
            ""
        }
    }
}