package com.example.postsapp

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTimestampToReadableFormat(timestamp: Long): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

    val date = Date(timestamp)
    return sdf.format(date)
}