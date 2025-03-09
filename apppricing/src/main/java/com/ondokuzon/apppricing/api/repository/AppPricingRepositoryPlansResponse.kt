package com.ondokuzon.apppricing.api.repository

import com.ondokuzon.apppricing.client.Plan

sealed class AppPricingRepositoryPlansResponse {
    object Idle : AppPricingRepositoryPlansResponse()
    object Loading : AppPricingRepositoryPlansResponse()
    data class Success(val plans: List<Plan>) : AppPricingRepositoryPlansResponse()
    data class Error(val exception: Exception) : AppPricingRepositoryPlansResponse()
}
