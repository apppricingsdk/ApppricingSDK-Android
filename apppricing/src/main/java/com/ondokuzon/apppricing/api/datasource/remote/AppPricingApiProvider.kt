package com.ondokuzon.apppricing.api.datasource.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val DEV_BASE_URL = "https://dash.apppricing.com/"
const val PROD_BASE_URL = "https://dash.apppricing.com/"

internal class AppPricingApiProvider {

    fun getDeviceDataApi(isDebug: Boolean): AppPricingApi {
        return createDeviceDataApi(
            retrofit = createRetrofitClient(
                okHttpClient = createOkHttpClient(isDebug),
                baseUrl = createBaseUrl(isDebug)
            )
        )
    }

    private fun createOkHttpClient(isDebug: Boolean): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(1L, TimeUnit.MINUTES)
            .readTimeout(1L, TimeUnit.MINUTES)
            .writeTimeout(1L, TimeUnit.MINUTES)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (isDebug) Level.BODY else Level.NONE
            })
            .build()
    }

    private fun createRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    private fun createBaseUrl(isDebug: Boolean): String {
        return when (isDebug) {
            true -> DEV_BASE_URL
            false -> PROD_BASE_URL
        }

    }

    private fun createDeviceDataApi(retrofit: Retrofit) = retrofit.create(AppPricingApi::class.java)
}