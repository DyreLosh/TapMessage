package ru.dyrelosh.tapmessage.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.dyrelosh.tapmessage.models.User

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val databaseRef: DatabaseReference = Firebase.database.reference
    var USER = User()
    val UID = firebaseAuth.currentUser?.uid.toString()
}

const val NODE_USERS = "users"
const val CHILD_ID = "id"
const val CHILD_EMAIL = "email"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullName"
const val CHILD_PHONE = "phone"
const val CHILD_STATE = "state"



