package com.ondokuzon.apppricing.api.datasource.remote.pages

internal sealed class PagesRemoteDataSourceResult {
    data class Success(
        val status: String,
        val message: String
    ) : PagesRemoteDataSourceResult()

    data class Error(val exception: Exception) : PagesRemoteDataSourceResult()
}
