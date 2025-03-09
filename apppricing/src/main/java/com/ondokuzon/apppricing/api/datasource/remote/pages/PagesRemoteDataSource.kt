package com.ondokuzon.apppricing.api.datasource.remote.pages

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.api.datasource.remote.model.PagesApiRequest
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage

internal class PagesRemoteDataSource(
    private val appPricingApi: AppPricingApi,
    private val loggingCallback: LoggingCallback
) {
    suspend fun postPage(
        apiKey: String,
        pagesRequest: PagesApiRequest
    ): PagesRemoteDataSourceResult {
        return try {
            val response = appPricingApi.postPage(apiKey, pagesRequest)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    PagesRemoteDataSourceResult.Success(
                        status = responseBody.status,
                        message = responseBody.message
                    )
                } ?: PagesRemoteDataSourceResult.Error(Exception("Empty response body"))
            } else {
                PagesRemoteDataSourceResult.Error(Exception("Request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            loggingCallback.log(LoggingMessage.ErrorMessage("Failed to post pages data", e))
            PagesRemoteDataSourceResult.Error(e)
        }
    }
}
