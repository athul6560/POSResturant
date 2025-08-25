package com.zeezaglobal.posresturant.Utils

import android.content.Context
import android.content.SharedPreferences

object StorePreferenceManager {
    private const val PREF_NAME = "login_prefs"
    private const val KEY_STORE_ID = "store_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveStoreId(context: Context, storeId: Int) {
        val editor = getPrefs(context).edit()
        editor.putInt(KEY_STORE_ID, storeId)
        editor.apply()
    }

    fun getStoreId(context: Context): Int {
        return getPrefs(context).getInt(KEY_STORE_ID, -1) // -1 means not logged in
    }
}