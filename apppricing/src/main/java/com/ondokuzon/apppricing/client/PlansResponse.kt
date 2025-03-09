package com.ondokuzon.apppricing.client

import com.google.gson.annotations.SerializedName

data class PlansResponse(
    @SerializedName("plans")
    val plans: List<Plan>
)

data class Plan(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
