package com.ondokuzon.apppricing

import android.content.Context
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryDeviceDataResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPlansResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryUserLocationResponse
import com.ondokuzon.apppricing.client.DeviceDataRequest
import com.ondokuzon.apppricing.error.ErrorCallback
import com.ondokuzon.apppricing.logging.LoggingCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object AppPricingInstance {
    private var appPricing: AppPricing? = null
    private val isInitialized = MutableStateFlow(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun initialize(
        context: Context,
        apiKey: String,
        isDebug: Boolean = false,
        errorCallback: ErrorCallback? = null,
        loggingCallback: LoggingCallback? = null,
        isLoggingEnabled: Boolean = false,
    ) {
        if (appPricing == null) {
            appPricing = AppPricing.Builder(context)
                .setApiKey(apiKey)
                .setErrorCallback(errorCallback)
                .setIsDebug(isDebug)
                .setLoggingCallback(loggingCallback)
                .setLoggingEnabled(isLoggingEnabled)
                .build()

            isInitialized.value = true
            initializeSession(DeviceDataRequest())
        }
    }

    fun isInitialized(): Flow<Boolean> = isInitialized.asStateFlow()

    private fun postDeviceData(deviceDataRequest: DeviceDataRequest) {
        scope.launch {
            appPricing?.postDeviceData(deviceDataRequest) ?: initializeError()
        }
    }

    private fun incrementSessionCount() {
        scope.launch {
            appPricing?.incrementSessionCount() ?: initializeError()
        }
    }

    fun postPage(pageName: String) {
        scope.launch {
            appPricing?.postPage(pageName) ?: initializeError()
        }
    }

    private suspend fun getUserLocation(): Flow<AppPricingRepositoryUserLocationResponse> {
        return appPricing?.getUserLocation() ?: initializeError()
    }

    suspend fun getDevicePlans(): Flow<AppPricingRepositoryPlansResponse> {
        return appPricing?.getDevicePlans() ?: initializeError()
    }

    suspend fun getDeviceDataResponse(): Flow<AppPricingRepositoryDeviceDataResponse>? {
        return appPricing?.getDeviceDataResponse()
    }

    private fun initializeSession(deviceDataRequest: DeviceDataRequest) {
        scope.launch {
            appPricing?.initializeSession(deviceDataRequest) ?: initializeError()
        }
    }
}

private fun initializeError(): Nothing =
    throw IllegalStateException("Did you forget to add AppPricingInstance.initialize() in your Application onCreate().")