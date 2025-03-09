package com.ondokuzon.apppricing.api.datasource.remote.session

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.api.datasource.remote.model.IncrementSessionResponse
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage

internal class SessionRemoteDataSource(
    private val appPricingApi: AppPricingApi,
    private val loggingCallback: LoggingCallback
) {
    suspend fun incrementSession(apiKey: String, deviceId: String): SessionRemoteDataSourceResult {
        return try {
            val response = appPricingApi.incrementSession(apiKey, deviceId)
            when {
                response.isSuccessful -> response.body().toDataSourceResult()
                else -> SessionRemoteDataSourceResult.Failed(IllegalStateException(response.message()))
            }
        } catch (e: Exception) {
            loggingCallback.log(LoggingMessage.ErrorMessage("Failed to increment session", e))
            SessionRemoteDataSourceResult.Failed(e)
        }
    }
}

private fun IncrementSessionResponse?.toDataSourceResult(): SessionRemoteDataSourceResult {
    this ?: return SessionRemoteDataSourceResult.Failed(IllegalStateException("Empty Body."))
    return SessionRemoteDataSourceResult.Success(
        message = message,
        sessionCount = sessionCount
    )
}
