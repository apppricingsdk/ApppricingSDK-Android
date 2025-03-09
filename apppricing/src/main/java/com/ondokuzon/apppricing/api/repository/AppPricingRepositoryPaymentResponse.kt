package com.ondokuzon.apppricing.api.repository

sealed class AppPricingRepositoryPaymentResponse {
    data object Loading : AppPricingRepositoryPaymentResponse()
    data object Success : AppPricingRepositoryPaymentResponse()
    data class Error(val exception: Exception) : AppPricingRepositoryPaymentResponse()
}
