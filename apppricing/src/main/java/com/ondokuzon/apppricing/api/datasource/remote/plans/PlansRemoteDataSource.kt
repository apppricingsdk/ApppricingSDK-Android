package com.ondokuzon.apppricing.api.datasource.remote.plans

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage

internal class PlansRemoteDataSource(
    private val api: AppPricingApi,
    private val loggingCallback: LoggingCallback
) {
    suspend fun getDevicePlans(apiKey: String, deviceId: String): PlansRemoteDataSourceResult {
        return try {
            val response = api.getDevicePlans(apiKey, deviceId)
            if (response.isSuccessful) {
                response.body()?.let { plansResponse ->
                    PlansRemoteDataSourceResult.Success(
                        plans = plansResponse.plans
                    )
                } ?: PlansRemoteDataSourceResult.Error(Exception("Empty response body"))
            } else {
                val error = Exception("Request failed with code: ${response.code()}")
                loggingCallback.log(LoggingMessage.ErrorMessage("Device Plans", error))
                PlansRemoteDataSourceResult.Error(error)
            }
        } catch (e: Exception) {
            loggingCallback.log(LoggingMessage.ErrorMessage("Device Plans", e))
            PlansRemoteDataSourceResult.Error(e)
        }
    }
}
