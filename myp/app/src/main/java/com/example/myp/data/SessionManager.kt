package com.example.myp.data

import android.content.Context

/**
 * 用 SharedPreferences 持久化登录态，App 重启后自动恢复，省去重复登录。
 */
object SessionManager {

    private const val PREFS = "myp_session"
    private const val KEY_USER_ID = "userId"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_IS_MALE = "isMale"
    private const val KEY_COUPLE_ID = "coupleId"
    private const val KEY_START_DATE = "startDate"
    private const val KEY_LOGGED_IN = "loggedIn"

    fun saveUser(context: Context, user: Map<String, Any>) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_LOGGED_IN, true)
            putLong(KEY_USER_ID, (user["userId"] as? Number)?.toLong() ?: 0L)
            putString(KEY_NICKNAME, user["nickname"] as? String ?: "")
            putBoolean(KEY_IS_MALE, user["isMale"] as? Boolean ?: true)
            putLong(KEY_COUPLE_ID, (user["coupleId"] as? Number)?.toLong() ?: 0L)
            putString(KEY_START_DATE, user["startDate"] as? String)
            apply()
        }
    }

    /** 已登录则返回还原的 user map，否则返回 null。 */
    fun loadUser(context: Context): Map<String, Any>? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_LOGGED_IN, false)) return null

        val user = mutableMapOf<String, Any>()
        user["userId"] = prefs.getLong(KEY_USER_ID, 0L)
        user["nickname"] = prefs.getString(KEY_NICKNAME, "") ?: ""
        user["isMale"] = prefs.getBoolean(KEY_IS_MALE, true)
        val coupleId = prefs.getLong(KEY_COUPLE_ID, 0L)
        if (coupleId > 0) user["coupleId"] = coupleId
        prefs.getString(KEY_START_DATE, null)?.let { user["startDate"] = it }
        return user
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
