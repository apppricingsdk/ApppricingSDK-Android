package com.ondokuzon.apppricing.api.repository

internal sealed class AppPricingRepositoryIncrementSessionResponse {

    data class Success(
        val message: String,
        val sessionCount: Int
    ) : AppPricingRepositoryIncrementSessionResponse()

    data class Failed(val exception: Exception) : AppPricingRepositoryIncrementSessionResponse()
}
