package com.ondokuzon.apppricing.api.repository

sealed class AppPricingRepositoryPagesResponse {
    object Idle : AppPricingRepositoryPagesResponse()
    object Loading : AppPricingRepositoryPagesResponse()
    data class Success(
        val status: String,
        val message: String
    ) : AppPricingRepositoryPagesResponse()
    data class Error(val exception: Exception) : AppPricingRepositoryPagesResponse()
}
