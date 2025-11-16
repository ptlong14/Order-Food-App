package com.longpt.projectll1.data.sharedPref

import android.content.Context
import androidx.core.content.edit

object UserStorage {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_AVATAR = "user_avatar"

    fun saveUser(context: Context, userName: String, userAvatar: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_AVATAR, userAvatar)
        }
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userName = prefs.getString(KEY_USER_NAME, "") ?: ""
        return userName
    }

    fun getAvatar(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userAvatar = prefs.getString(KEY_USER_AVATAR, "") ?: ""
        return userAvatar
    }

    fun clearUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { clear() }
    }
}