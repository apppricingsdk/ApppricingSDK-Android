package com.ondokuzon.apppricing.api.datasource.remote.location

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage

internal class UserLocationRemoteDataSource(
    private val api: AppPricingApi,
    private val loggingCallback: LoggingCallback
) {
    suspend fun getUserLocation(apiKey: String): UserLocationRemoteDataSourceResult {
        return try {
            val response = api.getUserLocation(apiKey)
            if (response.isSuccessful) {
                response.body()?.let { locationResponse ->
                    UserLocationRemoteDataSourceResult.Success(
                        country = locationResponse.country,
                        city = locationResponse.city,
                        region = locationResponse.region,
                    )
                } ?: UserLocationRemoteDataSourceResult.Error(Exception("Empty response body"))
            } else {
                val error = Exception("Request failed with code: ${response.code()}")
                loggingCallback.log(LoggingMessage.ErrorMessage("User Location", error))
                UserLocationRemoteDataSourceResult.Error(error)
            }
        } catch (e: Exception) {
            loggingCallback.log(LoggingMessage.ErrorMessage("User Location", e))
            UserLocationRemoteDataSourceResult.Error(e)
        }
    }
}