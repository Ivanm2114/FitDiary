package com.example.fitdiary

import com.example.fitdiary.data.api.ApiService
import com.example.fitdiary.data.api.LoginRequest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest

class NetworkApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: ApiService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun login_sendsBodyAndParsesResponse() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id":1,"nickname":"ivan"}""")
        )

        val response = api.login(LoginRequest(nickname = "ivan", password = "secret"))

        val recorded = server.takeRequest()
        assertEquals("/api/auth/login", recorded.path)
        val requestBody = json.decodeFromString<LoginRequest>(recorded.body.readUtf8())
        assertEquals("ivan", requestBody.nickname)
        assertEquals("secret", requestBody.password)

        val body = response.body()
        assertNotNull(body)
        assertEquals(1L, body?.id)
        assertEquals("ivan", body?.nickname)
    }

    @Test
    fun listRecipes_parsesResponseAndQuery() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """[
                      {
                        "id": 5,
                        "meal_type": "завтрак",
                        "name": "Омлет",
                        "calories": 350,
                        "recipe_text": "Яйца, молоко, соль...",
                        "image_url": "images/1.jpg"
                      }
                    ]"""
                )
        )

        val response = api.listRecipes(page = 1, limit = 10)

        val recorded = server.takeRequest()
        val url = recorded.requestUrl
        assertNotNull(url)
        assertEquals("/api/recipes", url?.encodedPath)
        assertEquals("1", url?.queryParameter("page"))
        assertEquals("10", url?.queryParameter("limit"))

        val body = response.body()
        assertNotNull(body)
        assertEquals(1, body?.size)
        assertEquals(5L, body?.first()?.id)
        assertEquals("Омлет", body?.first()?.name)
        assertEquals("завтрак", body?.first()?.mealType)
        assertEquals(350, body?.first()?.calories)
        assertEquals("images/1.jpg", body?.first()?.imageUrl)
    }
}
