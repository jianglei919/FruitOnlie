package com.conestoga.group12.fruitonline.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    const val FORMAT_YYYY_MM_DD: String = "yyyy-MM-dd HH:mm:ss"

    @JvmStatic
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat(FORMAT_YYYY_MM_DD, Locale.getDefault())
        return sdf.format(date)
    }
}
