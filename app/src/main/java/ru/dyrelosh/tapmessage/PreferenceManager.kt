package ru.dyrelosh.tapmessage

import android.content.Context
import ru.dyrelosh.tapmessage.utils.FirebaseUtils

class PreferenceManager(private val context: Context) {

    companion object {
        private const val PREFERENCE_ID = "id"
        private const val KEY_ID = "ID"
    }

    fun writeUserId(uid: String) {
        val preference = context.getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE)
        preference.edit().putString(KEY_ID, uid).apply()
    }

    fun readUserId(): String {
        val preference = context.getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE)
        return preference.getString(KEY_ID, null) ?: FirebaseUtils.UID
    }

    fun deleteUserId() {
        val preference = context.getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE)
        preference.edit().clear().apply()
    }
}