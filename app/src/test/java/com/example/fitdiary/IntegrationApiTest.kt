package com.example.fitdiary

import com.example.fitdiary.data.api.ApiService
import com.example.fitdiary.data.api.LoginRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class IntegrationApiTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val api: ApiService = Retrofit.Builder()
        .baseUrl("http://192.168.10.14:5000/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)

    @Test
    fun login_integration() = runTest {
        val response = api.login(LoginRequest(nickname = "ivan", password = "12345678"))
        assertNotNull(response.body())
    }

    @Test
    fun listRecipes_integration() = runTest {
        val response = api.listRecipes(page = 1, limit = 10)
        assertNotNull(response.body())
    }
}
