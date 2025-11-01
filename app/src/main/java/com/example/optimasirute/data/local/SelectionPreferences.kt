package com.example.optimasirute.data.local

import android.content.Context
import android.content.SharedPreferences

class SelectionPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wisata_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_IDS = "selected_ids"
    }

    fun saveSelectedIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_SELECTED_IDS, ids).apply()
    }

    fun getSelectedIds(): Set<String> {
        return prefs.getStringSet(KEY_SELECTED_IDS, emptySet()) ?: emptySet()
    }
}