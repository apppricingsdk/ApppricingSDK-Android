package com.ondokuzon.apppricing.client

import kotlinx.serialization.Serializable

@Serializable
data class PagesResponse(
    val status: String,
    val message: String
)
