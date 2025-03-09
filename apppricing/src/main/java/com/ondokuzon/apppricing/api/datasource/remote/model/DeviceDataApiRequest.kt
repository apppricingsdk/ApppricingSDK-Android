package com.ondokuzon.apppricing.api.datasource.remote.model

class DeviceDataApiRequest(
    val device_id: String?,
    val country: String?,
    val city: String?,
    val region: String?,
    val timezone: String?,
    val session_count: Int?,
    val language: String?,
    val brand: String?,
    val model: String?,
    val os: String?,
    val os_version: String?,
    val screen_height: Int?,
    val screen_width: Int?,
    val app_id:String,

    val manufacturer: String?,
    val device: String?,
    val product: String?,
    val board: String?,
    val hardware: String?,
    val android_version: String?,
    val build_id: String?,
    val build_time: Long?,
    val fingerprint: String?,
    val app_version: String?,
    val app_version_code: Long?,
    val first_install_time: Long?,
    val last_update_time: Long?,
    val total_memory: Long?,
    val available_memory: Long?,
    val number_of_cores: Int?,
)