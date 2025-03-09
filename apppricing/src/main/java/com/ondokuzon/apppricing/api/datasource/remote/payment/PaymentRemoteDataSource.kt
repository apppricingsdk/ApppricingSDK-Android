package com.ondokuzon.apppricing.api.datasource.remote.payment

import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApi
import com.ondokuzon.apppricing.api.datasource.remote.model.PaymentApiRequest
import com.ondokuzon.apppricing.error.ErrorCallback

internal class PaymentRemoteDataSource(
    private val api: AppPricingApi,
    private val apiKey: String,
    private val errorCallback: ErrorCallback?
) {
    suspend fun postPayment(request: PaymentApiRequest): PaymentRemoteDataSourceResult {
        return try {
            val response = api.postPayment(apiKey, request)
            if (response.isSuccessful) {
                PaymentRemoteDataSourceResult.Success
            } else {
                val errorMessage = "Failed to post payment: ${response.message()}"
                errorCallback?.onError(IllegalStateException(errorMessage))
                PaymentRemoteDataSourceResult.Error(IllegalStateException(errorMessage))
            }
        } catch (e: Exception) {
            errorCallback?.onError(e)
            PaymentRemoteDataSourceResult.Error(e)
        }
    }
}
