package com.ondokuzon.apppricing.client

data class PaymentRequest(
    val deviceId: String,
    val payments: List<Payment>
)

data class Payment(
    val details: String
)

internal fun PaymentRequest.toApiModel() = com.ondokuzon.apppricing.api.datasource.remote.model.PaymentApiRequest(
    deviceId = deviceId,
    payments = payments.map {
        com.ondokuzon.apppricing.api.datasource.remote.model.PaymentData(
            details = it.details
        )
    }
)
