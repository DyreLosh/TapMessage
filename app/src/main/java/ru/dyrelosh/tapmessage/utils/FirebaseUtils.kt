package ru.dyrelosh.tapmessage.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val databaseRef: DatabaseReference = Firebase.database.reference
    var USER = User()
    val UID = firebaseAuth.currentUser?.uid.toString()
    val storageRootRef: StorageReference = FirebaseStorage.getInstance().reference


}

const val NODE_USERS = "users"
const val CHILD_ID = "id"
const val CHILD_EMAIL = "email"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullName"
const val CHILD_PHONE = "phone"
const val CHILD_STATE = "state"
const val CHILD_PHOTO_URL = "photoUrl"

const val FOLDER_PROFILE_IMAGE = "profile_image"



