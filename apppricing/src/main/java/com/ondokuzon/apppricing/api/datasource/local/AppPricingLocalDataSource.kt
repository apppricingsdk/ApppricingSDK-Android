package com.ondokuzon.apppricing.api.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ondokuzon.apppricing.client.DeviceDataResponse
import kotlinx.coroutines.flow.Flow

internal class AppPricingLocalDataSource(
    private val context: Context,
) {
    private val dataStore: DataStore<DeviceDataResponse> = DataStoreFactory.create(
        serializer = DeviceDataSerializer(),
        produceFile = { context.dataStoreFile("app_pricing_device_data.pb") }
    )

    suspend fun saveDeviceDataResponse(deviceData: DeviceDataResponse) {
        dataStore.updateData { deviceData }
    }

    fun getDeviceDataResponse(): Flow<DeviceDataResponse> {
        return dataStore.data
    }
}