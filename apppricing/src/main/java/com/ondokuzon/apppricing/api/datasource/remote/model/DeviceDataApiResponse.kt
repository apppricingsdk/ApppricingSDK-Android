package com.ondokuzon.apppricing.api.datasource.remote.model

import com.ondokuzon.apppricing.client.DeviceDataResponse

internal class DeviceDataResponse(
    val status: String,
    val data: DeviceData,
) {
    fun toDeviceInfo(): DeviceDataResponse {
        return DeviceDataResponse(
            status = status,
            data = com.ondokuzon.apppricing.client.DeviceData(
                id = data.id,
                device_id = data.device_id,
                application_id = data.application_id,
            ),
        )
    }
}

internal data class DeviceData(
    val id: Int,
    val device_id: String,
    val application_id: Int,
)