package ru.dyrelosh.tapmessage.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.AppStates
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.USER
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef
import ru.dyrelosh.tapmessage.utils.NODE_USERS

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database =
            Firebase.database("https://tapmessage-7c890-default-rtdb.europe-west1.firebasedatabase.app")

        initUser()
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    private fun initUser() {
        databaseRef.child(NODE_USERS).child(UID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    USER = snapshot.getValue(User::class.java) ?: User()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}