package com.ondokuzon.apppricing.api.datasource.remote.location

sealed class UserLocationRemoteDataSourceResult {
    data class Success(
        val country: String,
        val city: String,
        val region: String,
    ) : UserLocationRemoteDataSourceResult()

    data class Error(val exception: Exception) : UserLocationRemoteDataSourceResult()
}
