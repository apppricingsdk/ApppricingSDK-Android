package com.ondokuzon.apppricing.api.repository

import com.ondokuzon.apppricing.client.DeviceDataResponse


sealed class AppPricingRepositoryDeviceDataResponse {

    data object Idle : AppPricingRepositoryDeviceDataResponse()

    data object Loading : AppPricingRepositoryDeviceDataResponse()

    data class Failed(val deviceDataResponse: DeviceDataResponse?, val throwable: Throwable) :
        AppPricingRepositoryDeviceDataResponse()

    data class Success(val deviceDataResponse: DeviceDataResponse) : AppPricingRepositoryDeviceDataResponse()

}