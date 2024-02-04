package com.vivek.quipmenttask.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatToString(dateFormat: String): String {
    // Format the date and time
    val simpleFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    return simpleFormat.format(this)
}