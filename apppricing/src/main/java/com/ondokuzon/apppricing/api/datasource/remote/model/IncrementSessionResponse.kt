package com.ondokuzon.apppricing.api.datasource.remote.model

import com.google.gson.annotations.SerializedName

internal data class IncrementSessionResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("session_count")
    val sessionCount: Int
)
