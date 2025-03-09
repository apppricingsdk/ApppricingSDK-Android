package com.ondokuzon.apppricing.api.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class PaymentApiRequest(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("payments")
    val payments: List<PaymentData>
)

data class PaymentData(
    @SerializedName("details")
    val details: String
)
