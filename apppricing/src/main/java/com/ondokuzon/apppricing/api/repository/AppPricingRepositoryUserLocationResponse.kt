package com.ondokuzon.apppricing.api.repository

sealed class AppPricingRepositoryUserLocationResponse {
    object Idle : AppPricingRepositoryUserLocationResponse()
    object Loading : AppPricingRepositoryUserLocationResponse()
    data class Success(
        val country: String,
        val city: String,
        val region: String,
    ) : AppPricingRepositoryUserLocationResponse()
    data class Error(val exception: Exception) : AppPricingRepositoryUserLocationResponse()
}
