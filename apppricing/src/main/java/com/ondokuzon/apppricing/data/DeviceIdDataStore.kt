package com.ondokuzon.apppricing.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

private val Context.deviceIdDataStore: DataStore<Preferences> by
preferencesDataStore(name = "app_pricing_device_id")
private val DEVICE_ID = stringPreferencesKey("device_id")

class DeviceIdDataStore(
    private val context: Context,
) {
    private val mutex = Mutex()
    
    private val deviceId: Flow<String> = context.deviceIdDataStore.data
        .map { preferences -> 
            preferences[DEVICE_ID] ?: getOrGenerateDeviceId()
        }

    private suspend fun getOrGenerateDeviceId(): String {
        return mutex.withLock {
            context.deviceIdDataStore.data.first()[DEVICE_ID] ?: generateAndSaveDeviceId()
        }
    }

    private suspend fun generateAndSaveDeviceId(): String {
        val newDeviceId = UUID.randomUUID().toString()
        context.deviceIdDataStore.edit { preferences ->
            preferences[DEVICE_ID] = newDeviceId
        }
        return newDeviceId
    }

    suspend fun getDeviceId(): String = deviceId.first()

    companion object {
        fun create(context: Context): DeviceIdDataStore {
            return DeviceIdDataStore(context)
        }
    }
}
