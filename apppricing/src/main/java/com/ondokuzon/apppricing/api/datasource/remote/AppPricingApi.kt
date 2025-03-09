package com.ondokuzon.apppricing.api.datasource.remote

import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.DeviceDataResponse
import com.ondokuzon.apppricing.api.datasource.remote.model.IncrementSessionResponse
import com.ondokuzon.apppricing.api.datasource.remote.model.PagesApiRequest
import com.ondokuzon.apppricing.api.datasource.remote.model.PaymentApiRequest
import com.ondokuzon.apppricing.client.PagesResponse
import com.ondokuzon.apppricing.client.PlansResponse
import com.ondokuzon.apppricing.client.UserLocationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

internal interface AppPricingApi {

    @POST("api/device-data")
    suspend fun postDeviceData(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: DeviceDataApiRequest
    ): Response<DeviceDataResponse>

    @POST("api/pages")
    suspend fun postPage(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: PagesApiRequest
    ): Response<PagesResponse>

    @GET("api/user-location")
    suspend fun getUserLocation(
        @Header("X-API-KEY") apiKey: String
    ): Response<UserLocationResponse>

    @GET("api/device-data/{device_id}/plans")
    suspend fun getDevicePlans(
        @Header("X-API-KEY") apiKey: String,
        @Path("device_id") deviceId: String
    ): Response<PlansResponse>

    @POST("api/device-data/{device_id}/increment-session")
    suspend fun incrementSession(
        @Header("X-API-KEY") apiKey: String,
        @Path("device_id") deviceId: String
    ): Response<IncrementSessionResponse>

    @POST("api/payments")
    suspend fun postPayment(
        @Header("X-API-KEY") apiKey: String,
        @Body requestBody: PaymentApiRequest
    ): Response<Unit>
}