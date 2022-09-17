package ru.dyrelosh.tapmessage.utils

import android.widget.Toast
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает...");

    companion object {
        fun updateState(appStates: AppStates) {
            databaseRef.child(NODE_USERS).child(UID).child(CHILD_STATE).setValue(appStates.state)
        }
    }
}

