package ru.dyrelosh.tapmessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database =
            Firebase.database("https://tapmessage-7c890-default-rtdb.europe-west1.firebasedatabase.app")

    }
}