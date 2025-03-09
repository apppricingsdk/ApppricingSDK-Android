package com.ondokuzon.apppricing

import android.content.Context
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryDeviceDataResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryIncrementSessionResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPagesResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPaymentResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPlansResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryUserLocationResponse
import com.ondokuzon.apppricing.client.DeviceDataRequest
import com.ondokuzon.apppricing.client.PagesRequest
import com.ondokuzon.apppricing.client.PaymentRequest
import com.ondokuzon.apppricing.error.ErrorCallback
import com.ondokuzon.apppricing.logging.LoggingCallback
import kotlinx.coroutines.flow.Flow

internal interface AppPricing {

    suspend fun postDeviceData(deviceDataRequest: DeviceDataRequest): Flow<AppPricingRepositoryDeviceDataResponse>
    
    suspend fun postPage(pageName: String): Flow<AppPricingRepositoryPagesResponse>

    suspend fun postPageRequest(pagesRequest: PagesRequest): Flow<AppPricingRepositoryPagesResponse>

    suspend fun getUserLocation(): Flow<AppPricingRepositoryUserLocationResponse>

    suspend fun getDevicePlans(): Flow<AppPricingRepositoryPlansResponse>

    suspend fun incrementSessionCount(): Flow<AppPricingRepositoryIncrementSessionResponse>

    suspend fun initializeSession(deviceDataRequest: DeviceDataRequest)

    suspend fun getDeviceDataResponse(): Flow<AppPricingRepositoryDeviceDataResponse>

    suspend fun postPayment(request: PaymentRequest): Flow<AppPricingRepositoryPaymentResponse>

    class Builder(context: Context) {

        private val context: Context = context.applicationContext
        private lateinit var apiKey: String
        private var isDebug: Boolean = false
        private var errorCallback: ErrorCallback? = null
        private var loggingCallback: LoggingCallback? = null
        private var isLoggingEnabled: Boolean = false

        fun setApiKey(apiKey: String): Builder {
            this.apiKey = apiKey
            return this
        }

        fun setErrorCallback(errorCallback: ErrorCallback?): Builder {
            this.errorCallback = errorCallback
            return this
        }

      fun setIsDebug(isDebug: Boolean): Builder {
            this.isDebug = isDebug
            return this
        }

        fun setLoggingCallback(loggingCallback: LoggingCallback?): Builder {
            this.loggingCallback = loggingCallback
            return this
        }

        fun setLoggingEnabled(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        fun build(): AppPricing {
            return AppPricingImpl(
                context,
                apiKey,
                isDebug,
                errorCallback,
                loggingCallback,
                isLoggingEnabled,
            )
        }
    }
}