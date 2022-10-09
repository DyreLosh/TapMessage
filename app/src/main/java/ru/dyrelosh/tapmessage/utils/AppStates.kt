package ru.dyrelosh.tapmessage.utils

import android.content.Context
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает...");

    companion object {
        fun updateState(appStates: AppStates, context: Context) {
            if (FirebaseUtils.userUid != null) {
                databaseRef.child(NODE_USERS).child(PreferenceManager(context).readUserId())
                    .child(CHILD_STATE).setValue(appStates.state)
            }

        }
    }
}

