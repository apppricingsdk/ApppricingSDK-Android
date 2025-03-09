package com.ondokuzon.apppricing.client

import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceInfoCollector

class DeviceDataRequest {

}

internal fun DeviceDataRequest.toApiModel(
    deviceId: String,
    country: String?,
    city: String?,
    region: String?,
    deviceInfoCollector: DeviceInfoCollector,
    sessionCount: Int
): DeviceDataApiRequest {
    val deviceInfo = deviceInfoCollector.collectDeviceInfo()
    return DeviceDataApiRequest(
        device_id = deviceId,
        country = country,
        city = city,
        region = region,
        timezone = deviceInfo.timeZone,
        session_count = sessionCount,
        language = deviceInfo.language,
        brand = deviceInfo.brand,
        model = deviceInfo.model,
        os = deviceInfo.os,
        os_version = deviceInfo.osVersion.toString(),
        screen_height = deviceInfo.screenMetrics.widthPixels,
        screen_width = deviceInfo.screenMetrics.heightPixels,
        app_id = deviceInfo.packageName,
        manufacturer = deviceInfo.manufacturer,
        device = deviceInfo.device,
        product = deviceInfo.product,
        board = deviceInfo.board,
        hardware = deviceInfo.hardware,
        android_version = deviceInfo.androidVersion,
        build_id = deviceInfo.buildId,
        build_time = deviceInfo.buildTime,
        fingerprint = deviceInfo.fingerprint,
        app_version = deviceInfo.appVersion,
        app_version_code = deviceInfo.appVersionCode,
        first_install_time = deviceInfo.firstInstallTime,
        last_update_time = deviceInfo.lastUpdateTime,
        total_memory = deviceInfo.totalMemory,
        available_memory = deviceInfo.availableMemory,
        number_of_cores = deviceInfo.numberOfCores,
        )
}