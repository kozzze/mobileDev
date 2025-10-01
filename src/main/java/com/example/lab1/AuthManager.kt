
package com.example.lab1

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREFS = "auth_prefs"
    private const val KEY_NAME = "user_name"
    private const val KEY_IS_ADMIN = "user_is_admin"

    fun saveUser(context: Context, name: String, isAdmin: Boolean) {
        prefs(context).edit()
            .putString(KEY_NAME, name)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }

    fun clearUser(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun getUserName(context: Context): String? = prefs(context).getString(KEY_NAME, null)

    fun isAdmin(context: Context): Boolean = prefs(context).getBoolean(KEY_IS_ADMIN, false)

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
