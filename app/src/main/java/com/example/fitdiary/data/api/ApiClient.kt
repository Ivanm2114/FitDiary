package com.example.fitdiary.data.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.fitdiary.metrics.MetricsTracker
import okhttp3.Interceptor
import okhttp3.Response


object ApiClient {
    const val baseUrl = "http://192.168.10.14:5000/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(MetricsInterceptor())
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}

private class MetricsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            MetricsTracker.recordRequest(response.isSuccessful)
            response
        } catch (exception: Exception) {
            MetricsTracker.recordRequest(false)
            throw exception
        }
    }
}
