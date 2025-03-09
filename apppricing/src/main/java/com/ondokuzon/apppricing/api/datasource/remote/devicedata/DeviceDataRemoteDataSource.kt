package com.ondokuzon.apppricing.api.datasource.remote.devicedata

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataResponse
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage

internal class DeviceDataRemoteDataSource(
    private val appPricingApi: AppPricingApi,
    private val loggingCallback: LoggingCallback
) {

    suspend fun postDeviceData(apiKey: String, deviceDataRequest: DeviceDataApiRequest): DeviceDataRemoteDataSourceResult {
        return try {
            val response = appPricingApi.postDeviceData(apiKey,deviceDataRequest)
            when {
                response.isSuccessful -> response.body().toDataSourceResult()
                else -> DeviceDataRemoteDataSourceResult.Failed(IllegalStateException(response.message()))
            }
        } catch (e: Exception) {
            loggingCallback.log(LoggingMessage.ErrorMessage("Failed to post device data", e))
            DeviceDataRemoteDataSourceResult.Failed(e)
        }
    }
}

private fun DeviceDataResponse?.toDataSourceResult(): DeviceDataRemoteDataSourceResult {
    this ?: return DeviceDataRemoteDataSourceResult.Failed(IllegalStateException("Empty Body."))
    return DeviceDataRemoteDataSourceResult.Success(
        deviceDataResponse = this.toDeviceInfo()
    )
}