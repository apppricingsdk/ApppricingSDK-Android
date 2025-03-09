package com.ondokuzon.apppricing.api.datasource.remote.devicedata

import com.ondokuzon.apppricing.client.DeviceDataResponse

internal class DeviceDataRemoteDataSourceResultMapper {

    fun mapToDeviceData(result: DeviceDataRemoteDataSourceResult.Success): DeviceDataResponse {
        return result.deviceDataResponse
    }
}