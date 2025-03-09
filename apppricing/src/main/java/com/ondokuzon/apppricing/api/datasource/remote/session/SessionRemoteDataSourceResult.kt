package com.ondokuzon.apppricing.api.datasource.remote.session

internal sealed class SessionRemoteDataSourceResult {
    data class Success(
        val message: String,
        val sessionCount: Int
    ) : SessionRemoteDataSourceResult()

    data class Failed(val exception: Exception) : SessionRemoteDataSourceResult()
}
