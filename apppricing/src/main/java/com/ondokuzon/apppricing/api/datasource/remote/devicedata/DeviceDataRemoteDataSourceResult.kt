package com.ondokuzon.apppricing.api.datasource.remote.devicedata

import com.ondokuzon.apppricing.client.DeviceDataResponse


internal sealed class DeviceDataRemoteDataSourceResult {
    data class Success(
        val deviceDataResponse: DeviceDataResponse,
    ) : DeviceDataRemoteDataSourceResult()

    data class Failed(val throwable: Throwable) : DeviceDataRemoteDataSourceResult()

}