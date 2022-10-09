package ru.dyrelosh.tapmessage.utils

import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)

}

