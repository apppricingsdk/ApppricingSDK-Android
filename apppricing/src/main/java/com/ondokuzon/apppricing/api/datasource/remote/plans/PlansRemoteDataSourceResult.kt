package com.ondokuzon.apppricing.api.datasource.remote.plans

import com.ondokuzon.apppricing.client.Plan

sealed class PlansRemoteDataSourceResult {
    data class Success(
        val plans: List<Plan>
    ) : PlansRemoteDataSourceResult()

    data class Error(val exception: Exception) : PlansRemoteDataSourceResult()
}
