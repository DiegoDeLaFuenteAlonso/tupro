package com.diego.tupro

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPref", 0)

    fun saveUserDetails(email: String, username: String) {
        val editor = prefs.edit()
        editor.putString("Email", email)
        editor.putString("Username", username)
        editor.apply()
    }

    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user["email"] = prefs.getString("Email", "")!!
        user["username"] = prefs.getString("Username", "")!!
        return user
    }

    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
