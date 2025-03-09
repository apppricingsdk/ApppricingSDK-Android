package com.ondokuzon.apppricing.client

import kotlinx.serialization.Serializable

@Serializable
data class DeviceDataResponse(
    val status: String?,
    val data: DeviceData?,
){
    companion object {
        fun empty() = DeviceDataResponse(
            status = null,
            data = null,
        )
    }
}

@Serializable
data class DeviceData(
    val id: Int?,
    val device_id: String?,
    val application_id: Int?,
)