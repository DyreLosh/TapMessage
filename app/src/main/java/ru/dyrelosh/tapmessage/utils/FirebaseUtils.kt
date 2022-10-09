package ru.dyrelosh.tapmessage.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
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
const val IMAGE_TYPE = "image"

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
const val CHILD_IMAGE_URL = "imageUrl"


const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSAGES_IMAGES = "message_image"

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

fun sendMessageAsImage(receivingUserId: String, imageUrl: String, messageKey: String) {
    val refDialogUser = "$NODE_MESSAGES/${FirebaseUtils.userUid}/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/${FirebaseUtils.userUid}"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = FirebaseUtils.userUid
    mapMessage[CHILD_TYPE] = IMAGE_TYPE
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_IMAGE_URL] = imageUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    FirebaseUtils.databaseRef.updateChildren(mapDialog).addOnSuccessListener {

    }
}
fun putUrlToDatabase(url: String, function: () -> Unit) {
    FirebaseUtils.databaseRef.child(NODE_USERS).child(FirebaseUtils.userUid)
        .child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            Toast.makeText(APP_ACTIVITY, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
}

fun getUrlFromStorage(
    path: StorageReference,
    function: (url: String) -> Unit
) {
    path.downloadUrl.addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener {
            Toast.makeText(APP_ACTIVITY, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
}

fun putImageToStorage(
    url: Uri,
    path: StorageReference,
    function: () -> Unit
) {
    path.putFile(url).addOnSuccessListener { function() }
        .addOnFailureListener {
            Toast.makeText(APP_ACTIVITY, it.message.toString(), Toast.LENGTH_SHORT).show()
        }

}

fun DataSnapshot.getCommonModel(): Common =
    this.getValue(Common::class.java) ?: Common()

fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User()