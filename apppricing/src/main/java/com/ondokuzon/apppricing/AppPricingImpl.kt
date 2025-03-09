package com.ondokuzon.apppricing

import android.content.Context
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceInfoCollector
import com.ondokuzon.apppricing.api.repository.AppPricingRepository
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryDeviceDataResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryIncrementSessionResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPagesResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPaymentResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPlansResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryUserLocationResponse
import com.ondokuzon.apppricing.billing.BillingManager
import com.ondokuzon.apppricing.billing.SubscriptionState
import com.ondokuzon.apppricing.client.DeviceDataRequest
import com.ondokuzon.apppricing.client.PagesRequest
import com.ondokuzon.apppricing.client.Payment
import com.ondokuzon.apppricing.client.PaymentRequest
import com.ondokuzon.apppricing.client.toApiModel
import com.ondokuzon.apppricing.data.DeviceIdDataStore
import com.ondokuzon.apppricing.data.SessionCountDataStore
import com.ondokuzon.apppricing.data.UserLocationDataStore
import com.ondokuzon.apppricing.error.ErrorCallback
import com.ondokuzon.apppricing.lifecycle.AppPricingLifecycleObserver
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingCallbackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

internal class AppPricingImpl(
    appContext: Context,
    apiKey: String,
    isDebug: Boolean = false,
    errorCallback: ErrorCallback?,
    loggingCallback: LoggingCallback?,
    isLoggingEnabled: Boolean,
) : AppPricing {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val loggingCallbackAdapter: LoggingCallback =
        LoggingCallbackAdapter(isLoggingEnabled, loggingCallback)

    private val deviceInfoCollector = DeviceInfoCollector(appContext)

    private val billingManager = BillingManager(
        context = appContext,
        coroutineScope = coroutineScope
    )

    private val appPricingRepository = AppPricingRepository.create(
        appContext = appContext,
        apiKey = apiKey,
        isDebug = isDebug,
        loggingCallback = loggingCallbackAdapter,
        coroutineScope = coroutineScope,
        errorCallback = errorCallback
    )

    private val sessionCountDataStore = SessionCountDataStore.create(appContext)
    private val deviceIdDataStore = DeviceIdDataStore.create(appContext)
    private val userLocationDataStore = UserLocationDataStore.create(appContext)

    private val lifecycleObserver = AppPricingLifecycleObserver(
        sessionCountDataStore = sessionCountDataStore,
        repository = appPricingRepository,
        deviceIdDataStore = deviceIdDataStore,
        coroutineScope = coroutineScope
    )

    init {
        (appContext.applicationContext as? android.app.Application)?.registerActivityLifecycleCallbacks(
            lifecycleObserver
        )

        coroutineScope.launch {
            billingManager.subscriptionState.collectLatest { state ->
                when (state) {
                    is SubscriptionState.Active -> {
                        val deviceId = deviceIdDataStore.getDeviceId()
                        
                        postPayment(
                            PaymentRequest(
                                deviceId = deviceId,
                                payments = listOf(
                                    Payment(
                                        details = state.purchaseToken
                                    )
                                )
                            )
                        )
                    }
                    else -> { // NO-OP }
                }
            }
        }
    }
    }

    override suspend fun postDeviceData(deviceDataRequest: DeviceDataRequest): Flow<AppPricingRepositoryDeviceDataResponse> {
        val deviceId = deviceIdDataStore.getDeviceId()
        val sessionCount = sessionCountDataStore.getSessionCount()
        var country = userLocationDataStore.country.firstOrNull()
        var city = userLocationDataStore.city.firstOrNull()
        var region = userLocationDataStore.region.firstOrNull()

        if (country == null || city == null || region == null) {
            appPricingRepository.getUserLocation()
                .filter { response ->
                    response is AppPricingRepositoryUserLocationResponse.Success ||
                            response is AppPricingRepositoryUserLocationResponse.Error
                }
                .first()
                .let { response ->
                    when (response) {
                        is AppPricingRepositoryUserLocationResponse.Success -> {
                            country = response.country
                            city = response.city
                            region = response.region
                        }

                        is AppPricingRepositoryUserLocationResponse.Error -> {
                            country = ""
                            city = ""
                        }

                        else -> {}
                    }
                }
        }

        return appPricingRepository.postDeviceData(
            deviceDataRequest.toApiModel(
                deviceId = deviceId,
                country = country ?: "",
                city = city ?: "",
                region = region ?: "",
                deviceInfoCollector = deviceInfoCollector,
                sessionCount = sessionCount
            )
        )
    }

    override suspend fun postPage(pageName: String): Flow<AppPricingRepositoryPagesResponse> {
        val deviceId = deviceIdDataStore.getDeviceId()
        return postPageRequest(
            PagesRequest(
                deviceId = deviceId,
                pageName = pageName
            )
        )
    }

    override suspend fun postPageRequest(pagesRequest: PagesRequest): Flow<AppPricingRepositoryPagesResponse> {
        return appPricingRepository.postPage(pagesRequest.toApiModel())
    }

    override suspend fun getUserLocation(): Flow<AppPricingRepositoryUserLocationResponse> {
        return appPricingRepository.getUserLocation()
    }

    override suspend fun getDevicePlans(): Flow<AppPricingRepositoryPlansResponse> {
        val deviceId = deviceIdDataStore.getDeviceId()
        return appPricingRepository.getDevicePlans(deviceId)
    }

    override suspend fun incrementSessionCount(): Flow<AppPricingRepositoryIncrementSessionResponse> {
        val deviceId = deviceIdDataStore.getDeviceId()
        return appPricingRepository.incrementSession(deviceId)
    }

    override suspend fun initializeSession(deviceDataRequest: DeviceDataRequest) {
        val deviceId = deviceIdDataStore.getDeviceId()
        val sessionCount = sessionCountDataStore.getSessionCount()
        var country = userLocationDataStore.country.firstOrNull()
        var city = userLocationDataStore.city.firstOrNull()
        var region = userLocationDataStore.region.firstOrNull()

        if (country == null || city == null || region == null) {
            appPricingRepository.getUserLocation()
                .filter { response ->
                    response is AppPricingRepositoryUserLocationResponse.Success ||
                            response is AppPricingRepositoryUserLocationResponse.Error
                }
                .first()
                .let { response ->
                    when (response) {
                        is AppPricingRepositoryUserLocationResponse.Success -> {
                            country = response.country
                            city = response.city
                            region = response.region
                        }

                        is AppPricingRepositoryUserLocationResponse.Error -> {
                            country = ""
                            city = ""
                        }

                        else -> {}
                    }
                }
        }

        appPricingRepository.initializeSession(
            deviceDataRequest.toApiModel(
                deviceId = deviceId,
                country = country ?: "",
                city = city ?: "",
                region = region ?: "",
                deviceInfoCollector = deviceInfoCollector,
                sessionCount = sessionCount
            )
        )
    }

    override suspend fun getDeviceDataResponse(): Flow<AppPricingRepositoryDeviceDataResponse> {
        return appPricingRepository.getDeviceDataResponse()
    }

    override suspend fun postPayment(request: PaymentRequest): Flow<AppPricingRepositoryPaymentResponse> {
        return appPricingRepository.postPayment(request.toApiModel())
    }
}