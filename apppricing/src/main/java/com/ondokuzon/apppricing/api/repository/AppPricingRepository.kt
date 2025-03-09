package com.ondokuzon.apppricing.api.repository

import android.content.Context
import com.ondokuzon.apppricing.api.datasource.local.AppPricingLocalDataSource
import com.ondokuzon.apppricing.api.datasource.remote.AppPricingApiProvider
import com.ondokuzon.apppricing.api.datasource.remote.devicedata.DeviceDataRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.devicedata.DeviceDataRemoteDataSourceResult
import com.ondokuzon.apppricing.api.datasource.remote.devicedata.DeviceDataRemoteDataSourceResultMapper
import com.ondokuzon.apppricing.api.datasource.remote.location.UserLocationRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.location.UserLocationRemoteDataSourceResult
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.PagesApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.PaymentApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.pages.PagesRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.pages.PagesRemoteDataSourceResult
import com.ondokuzon.apppricing.api.datasource.remote.payment.PaymentRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.payment.PaymentRemoteDataSourceResult
import com.ondokuzon.apppricing.api.datasource.remote.plans.PlansRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.plans.PlansRemoteDataSourceResult
import com.ondokuzon.apppricing.api.datasource.remote.session.SessionRemoteDataSource
import com.ondokuzon.apppricing.api.datasource.remote.session.SessionRemoteDataSourceResult
import com.ondokuzon.apppricing.data.UserLocationDataStore
import com.ondokuzon.apppricing.error.ErrorCallback
import com.ondokuzon.apppricing.logging.LoggingCallback
import com.ondokuzon.apppricing.logging.LoggingMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class AppPricingRepository(
    private val deviceDataRemoteDataSource: DeviceDataRemoteDataSource,
    private val deviceDataRemoteDataSourceMapper: DeviceDataRemoteDataSourceResultMapper,
    private val pagesRemoteDataSource: PagesRemoteDataSource,
    private val userLocationRemoteDataSource: UserLocationRemoteDataSource,
    private val plansRemoteDataSource: PlansRemoteDataSource,
    private val sessionRemoteDataSource: SessionRemoteDataSource,
    private val paymentRemoteDataSource: PaymentRemoteDataSource,
    private val appPricingLocalDataSource: AppPricingLocalDataSource,
    private val userLocationDataStore: UserLocationDataStore,
    private val apiKey: String,
    private val loggingCallback: LoggingCallback,
    private val errorCallback: ErrorCallback?,
    private val coroutineScope: CoroutineScope,
) {
    private val deviceDataRequestStateFlow =
        MutableStateFlow<AppPricingRepositoryDeviceDataResponse>(
            AppPricingRepositoryDeviceDataResponse.Idle
        )

    private val pagesRequestStateFlow =
        MutableStateFlow<AppPricingRepositoryPagesResponse>(
            AppPricingRepositoryPagesResponse.Idle
        )

    private val getUserLocationRequestStateFlow =
        MutableStateFlow<AppPricingRepositoryUserLocationResponse>(
            AppPricingRepositoryUserLocationResponse.Idle
        )

    private val getDevicePlansStateFlow =
        MutableStateFlow<AppPricingRepositoryPlansResponse>(
            AppPricingRepositoryPlansResponse.Idle
        )

    private val incrementSessionStateFlow =
        MutableStateFlow<AppPricingRepositoryIncrementSessionResponse>(
            AppPricingRepositoryIncrementSessionResponse.Failed(IllegalStateException("Not initialized"))
        )

    private val paymentStateFlow =
        MutableStateFlow<AppPricingRepositoryPaymentResponse>(AppPricingRepositoryPaymentResponse.Loading)

    suspend fun postDeviceData(deviceDataRequest: DeviceDataApiRequest): Flow<AppPricingRepositoryDeviceDataResponse> {
        if (deviceDataRequestStateFlow.value !is AppPricingRepositoryDeviceDataResponse.Loading) {
            deviceDataRequestStateFlow.emit(AppPricingRepositoryDeviceDataResponse.Loading)
            coroutineScope.launch { postDeviceDataInternally(deviceDataRequest) }
        }
        return deviceDataRequestStateFlow
    }

    suspend fun postPage(pagesRequest: PagesApiRequest): Flow<AppPricingRepositoryPagesResponse> {
        val savedDeviceData = appPricingLocalDataSource.getDeviceDataResponse().first()

        if (savedDeviceData.data == null) {
            return flow { AppPricingRepositoryPagesResponse.Idle }
        }else{
            if (pagesRequestStateFlow.value !is AppPricingRepositoryPagesResponse.Loading) {
                pagesRequestStateFlow.emit(AppPricingRepositoryPagesResponse.Loading)
                coroutineScope.launch { postPagesInternally(pagesRequest) }
            }
            return pagesRequestStateFlow
        }
    }

    suspend fun getUserLocation(): Flow<AppPricingRepositoryUserLocationResponse> {
        if (getUserLocationRequestStateFlow.value !is AppPricingRepositoryUserLocationResponse.Loading) {
            getUserLocationRequestStateFlow.emit(AppPricingRepositoryUserLocationResponse.Loading)
            coroutineScope.launch { getUserLocationInternally() }
        }
        return getUserLocationRequestStateFlow
    }

    suspend fun getDevicePlans(deviceId: String): Flow<AppPricingRepositoryPlansResponse> {
        if (getDevicePlansStateFlow.value !is AppPricingRepositoryPlansResponse.Loading) {
            getDevicePlansStateFlow.emit(AppPricingRepositoryPlansResponse.Loading)
            coroutineScope.launch { getDevicePlansInternally(deviceId) }
        }
        return getDevicePlansStateFlow
    }

    suspend fun incrementSession(deviceId: String): Flow<AppPricingRepositoryIncrementSessionResponse> {
        coroutineScope.launch {
            when (val result = sessionRemoteDataSource.incrementSession(apiKey, deviceId)) {
                is SessionRemoteDataSourceResult.Success -> {
                    incrementSessionStateFlow.emit(
                        AppPricingRepositoryIncrementSessionResponse.Success(
                            message = result.message,
                            sessionCount = result.sessionCount
                        )
                    )
                }
                is SessionRemoteDataSourceResult.Failed -> {
                    errorCallback?.onError(result.exception)
                    incrementSessionStateFlow.emit(
                        AppPricingRepositoryIncrementSessionResponse.Failed(result.exception)
                    )
                }
            }
        }
        return incrementSessionStateFlow
    }

    suspend fun postPayment(request: PaymentApiRequest): Flow<AppPricingRepositoryPaymentResponse> {
        paymentStateFlow.emit(AppPricingRepositoryPaymentResponse.Loading)
        coroutineScope.launch {
            when (val result = paymentRemoteDataSource.postPayment(request)) {
                is PaymentRemoteDataSourceResult.Success -> {
                    paymentStateFlow.emit(AppPricingRepositoryPaymentResponse.Success)
                }
                is PaymentRemoteDataSourceResult.Error -> {
                    paymentStateFlow.emit(AppPricingRepositoryPaymentResponse.Error(result.exception))
                }
            }
        }
        return paymentStateFlow
    }

    suspend fun initializeSession(deviceDataRequest: DeviceDataApiRequest) {
        val savedDeviceData = appPricingLocalDataSource.getDeviceDataResponse().first()

        if (savedDeviceData.data == null) {
            postDeviceData(deviceDataRequest)
        } else {
            incrementSession(savedDeviceData.data.device_id!!)
        }
    }

    fun getDeviceDataResponse(): Flow<AppPricingRepositoryDeviceDataResponse> {
        return appPricingLocalDataSource.getDeviceDataResponse()
            .map { deviceData ->
                if (deviceData.data != null) {
                    AppPricingRepositoryDeviceDataResponse.Success(deviceData)
                } else {
                    val lastResponse = appPricingLocalDataSource.getDeviceDataResponse().first()
                    AppPricingRepositoryDeviceDataResponse.Failed(
                        lastResponse,
                        IllegalStateException("No device data available")
                    )
                }
            }
    }

    private suspend fun postDeviceDataInternally(deviceDataRequest: DeviceDataApiRequest) {
        when (val deviceDataResult =
            deviceDataRemoteDataSource.postDeviceData(apiKey, deviceDataRequest)) {
            is DeviceDataRemoteDataSourceResult.Success -> {
                val deviceData =
                    deviceDataRemoteDataSourceMapper.mapToDeviceData(deviceDataResult)
                appPricingLocalDataSource.saveDeviceDataResponse(deviceData)
                loggingCallback.log(LoggingMessage.InfoMessage("Device Data: $deviceData"))
                deviceDataRequestStateFlow.emit(
                    AppPricingRepositoryDeviceDataResponse.Success(
                        deviceData
                    )
                )
            }

            is DeviceDataRemoteDataSourceResult.Failed -> {
                val lastResponse = appPricingLocalDataSource.getDeviceDataResponse().first()
                errorCallback?.onError(deviceDataResult.throwable)
                loggingCallback.log(
                    LoggingMessage.ErrorMessage(
                        "Device Data",
                        deviceDataResult.throwable
                    )
                )
                deviceDataRequestStateFlow.emit(
                    AppPricingRepositoryDeviceDataResponse.Failed(
                        lastResponse,
                        deviceDataResult.throwable
                    )
                )
            }
        }
    }

    private suspend fun postPagesInternally(pagesRequest: PagesApiRequest) {
        when (val result = pagesRemoteDataSource.postPage(apiKey, pagesRequest)) {
            is PagesRemoteDataSourceResult.Success -> {
                loggingCallback.log(LoggingMessage.InfoMessage("Post Page: $result"))
                pagesRequestStateFlow.emit(
                    AppPricingRepositoryPagesResponse.Success(
                        status = result.status,
                        message = result.message
                    )
                )
            }

            is PagesRemoteDataSourceResult.Error -> {
                errorCallback?.onError(result.exception)
                loggingCallback.log(
                    LoggingMessage.ErrorMessage(
                        "Post Page",
                        result.exception
                    )
                )
                pagesRequestStateFlow.emit(AppPricingRepositoryPagesResponse.Error(result.exception))
            }
        }
    }

    private suspend fun getUserLocationInternally() {
        when (val result = userLocationRemoteDataSource.getUserLocation(apiKey)) {
            is UserLocationRemoteDataSourceResult.Success -> {
                loggingCallback.log(LoggingMessage.InfoMessage("User Location: $result"))
                userLocationDataStore.saveLocation(result.country, result.city, result.region)
                getUserLocationRequestStateFlow.emit(
                    AppPricingRepositoryUserLocationResponse.Success(
                        country = result.country,
                        city = result.city,
                        region = result.region
                    )
                )
            }
            is UserLocationRemoteDataSourceResult.Error -> {
                errorCallback?.onError(result.exception)
                loggingCallback.log(
                    LoggingMessage.ErrorMessage(
                        "User Location",
                        result.exception
                    )
                )
                getUserLocationRequestStateFlow.emit(
                    AppPricingRepositoryUserLocationResponse.Error(result.exception)
                )
            }
        }
    }

    private suspend fun getDevicePlansInternally(deviceId: String) {
        when (val result = plansRemoteDataSource.getDevicePlans(apiKey, deviceId)) {
            is PlansRemoteDataSourceResult.Success -> {
                loggingCallback.log(LoggingMessage.InfoMessage("Device Plan: $result"))
                getDevicePlansStateFlow.emit(
                    AppPricingRepositoryPlansResponse.Success(
                        plans = result.plans
                    )
                )
            }
            is PlansRemoteDataSourceResult.Error -> {
                errorCallback?.onError(result.exception)
                loggingCallback.log(
                    LoggingMessage.ErrorMessage(
                        "Device Plan",
                        result.exception
                    )
                )
                getDevicePlansStateFlow.emit(
                    AppPricingRepositoryPlansResponse.Error(result.exception)
                )
            }
        }
    }

    companion object {
        fun create(
            appContext: Context,
            apiKey: String,
            isDebug: Boolean,
            loggingCallback: LoggingCallback,
            coroutineScope: CoroutineScope,
            errorCallback: ErrorCallback?
        ): AppPricingRepository {
            val subscriptionAPI = AppPricingApiProvider().getDeviceDataApi(isDebug)
            val deviceDataRemoteDataSource =
                DeviceDataRemoteDataSource(subscriptionAPI, loggingCallback)
            val deviceDataRemoteDataSourceResultMapper = DeviceDataRemoteDataSourceResultMapper()
            val pagesRemoteDataSource = PagesRemoteDataSource(subscriptionAPI, loggingCallback)
            val userLocationRemoteDataSource = UserLocationRemoteDataSource(subscriptionAPI, loggingCallback)
            val plansRemoteDataSource = PlansRemoteDataSource(subscriptionAPI, loggingCallback)
            val sessionRemoteDataSource = SessionRemoteDataSource(subscriptionAPI, loggingCallback)
            val appPricingLocalDataSource = AppPricingLocalDataSource(appContext)
            val paymentRemoteDataSource = PaymentRemoteDataSource(subscriptionAPI, apiKey, errorCallback)
            val userLocationDataStore = UserLocationDataStore.create(appContext)

            return AppPricingRepository(
                deviceDataRemoteDataSource,
                deviceDataRemoteDataSourceResultMapper,
                pagesRemoteDataSource,
                userLocationRemoteDataSource,
                plansRemoteDataSource,
                sessionRemoteDataSource,
                paymentRemoteDataSource,
                appPricingLocalDataSource,
                userLocationDataStore,
                apiKey,
                loggingCallback,
                errorCallback,
                coroutineScope
            )
        }
    }
}