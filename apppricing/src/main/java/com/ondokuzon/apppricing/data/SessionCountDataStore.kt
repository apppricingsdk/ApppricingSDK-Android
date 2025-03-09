package com.ondokuzon.apppricing.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by
preferencesDataStore(name = "app_pricing_session_counter")
private val SESSION_COUNT = intPreferencesKey("session_count")

class SessionCountDataStore(
    private val context: Context,
) {
    private val sessionCount: Flow<Int> = context.dataStore.data
        .map { preferences -> 
            preferences[SESSION_COUNT] ?: 0 
        }

    suspend fun getSessionCount(): Int = sessionCount.map { it }.first()

    suspend fun increaseSessionCount() {
        context.dataStore.edit { preferences ->
            val currentCount = preferences[SESSION_COUNT] ?: 0
            preferences[SESSION_COUNT] = currentCount + 1
        }
    }

    companion object {
        fun create(context: Context): SessionCountDataStore {
            return SessionCountDataStore(context)
        }
    }
}
