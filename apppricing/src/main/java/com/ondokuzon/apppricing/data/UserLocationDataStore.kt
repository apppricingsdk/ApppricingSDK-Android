package com.ondokuzon.apppricing.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userLocationDataStore: DataStore<Preferences> by
preferencesDataStore(name = "app_pricing_user_location")

private val COUNTRY = stringPreferencesKey("country")
private val CITY = stringPreferencesKey("city")
private val REGION = stringPreferencesKey("region")

class UserLocationDataStore(
    private val context: Context,
) {
    val country: Flow<String?> = context.userLocationDataStore.data
        .map { preferences -> preferences[COUNTRY] }

    val city: Flow<String?> = context.userLocationDataStore.data
        .map { preferences -> preferences[CITY] }

    val region: Flow<String?> = context.userLocationDataStore.data
        .map { preferences -> preferences[REGION] }

    suspend fun saveLocation(country: String, city: String, region: String) {
        context.userLocationDataStore.edit { preferences ->
            preferences[COUNTRY] = country
            preferences[CITY] = city
            preferences[REGION] = region
        }
    }

    companion object {
        fun create(context: Context): UserLocationDataStore {
            return UserLocationDataStore(context)
        }
    }
}
