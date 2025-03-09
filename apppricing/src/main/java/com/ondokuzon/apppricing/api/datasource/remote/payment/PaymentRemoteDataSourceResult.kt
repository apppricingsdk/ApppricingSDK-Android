package com.ondokuzon.apppricing.api.datasource.remote.payment

sealed class PaymentRemoteDataSourceResult {
    data object Success : PaymentRemoteDataSourceResult()
    data class Error(val exception: Exception) : PaymentRemoteDataSourceResult()
}
