package com.example.mindsync.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private val dataStore = context.dataStore

    val userEmail: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL]
        }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val lastResetDate: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[LAST_RESET_DATE]
        }

    val quickNotes: Flow<List<String>> = dataStore.data
        .map { preferences ->
            preferences[QUICK_NOTES]?.split("|||")?.filter { it.isNotBlank() } ?: emptyList()
        }

    suspend fun saveQuickNotes(notes: List<String>) {
        dataStore.edit { preferences ->
            preferences[QUICK_NOTES] = notes.joinToString("|||")
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveLastResetDate(date: String) {
        dataStore.edit { preferences ->
            preferences[LAST_RESET_DATE] = date
        }
    }

    companion object {
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        private val QUICK_NOTES = stringPreferencesKey("quick_notes")
    }
}
