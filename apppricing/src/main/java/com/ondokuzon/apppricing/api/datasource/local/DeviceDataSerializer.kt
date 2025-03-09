package com.ondokuzon.apppricing.api.datasource.local

import androidx.datastore.core.Serializer
import com.ondokuzon.apppricing.client.DeviceDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class DeviceDataSerializer : Serializer<DeviceDataResponse> {
    override val defaultValue: DeviceDataResponse
        get() = DeviceDataResponse(null, null)

    override suspend fun readFrom(input: InputStream): DeviceDataResponse {
        return try {
            Json.decodeFromString(
                deserializer = DeviceDataResponse.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(data: DeviceDataResponse, output: OutputStream) {
        withContext(Dispatchers.IO){
            output.write(
                Json.encodeToString(DeviceDataResponse.serializer(), data)
                    .encodeToByteArray()
            )
        }
    }
}
