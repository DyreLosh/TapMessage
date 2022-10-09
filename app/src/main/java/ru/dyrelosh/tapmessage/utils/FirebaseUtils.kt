package ru.dyrelosh.tapmessage.utils

import android.annotation.SuppressLint
import android.provider.ContactsContract
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.models.User
import java.util.ArrayList

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val databaseRef: DatabaseReference = Firebase.database.reference
    var USER = User()
    val UID = firebaseAuth.currentUser?.uid.toString()
    val storageRootRef: StorageReference = FirebaseStorage.getInstance().reference
    val userUid = PreferenceManager(APP_ACTIVITY).readUserId()

}

const val TEXT_TYPE = "text"

const val NODE_USERS = "users"
const val NODE_EMAILS = "emails"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_MESSAGES = "messages"


const val CHILD_ID = "id"
const val CHILD_EMAIL = "email"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullName"
const val CHILD_PHONE = "phone"
const val CHILD_STATE = "state"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"

const val FOLDER_PROFILE_IMAGE = "profile_image"


@SuppressLint("Range")
fun initContacts() {
    if (checkPermissions(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<Common>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (cursor.moveToNext()) {
                val fullName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = Common()
                newModel.fullName = fullName
                newModel.phone =
                    phone.replace(Regex("[\\s, -]"), "").replace(Regex("[\\s, ()]"), "")
                arrayContacts.add(newModel)
            }

        }
        cursor?.close()
        updatePhonesToDatabase(arrayContacts)

    }
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<Common>) {
    FirebaseUtils.databaseRef.child(NODE_PHONES)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { data ->
                    arrayContacts.forEach { contact ->
                        if (data.key == contact.phone) {
                            FirebaseUtils.databaseRef.child(NODE_PHONES_CONTACTS).child(
                                PreferenceManager(
                                    APP_ACTIVITY
                                ).readUserId()
                            ).child(data.value.toString()).child(CHILD_ID)
                                .setValue(data.value.toString()).addOnFailureListener {
                                Toast.makeText(
                                    APP_ACTIVITY,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
}

fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()

fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User()