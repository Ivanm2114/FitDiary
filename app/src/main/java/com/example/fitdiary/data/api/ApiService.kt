package com.example.fitdiary.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(
        @Body request: CreateUserRequest,
    ): Response<CreateUserResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<CreateUserResponse>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest,
    ): Response<UpdateUserResponse>

    @POST("api/meals")
    suspend fun createMeal(
        @Body request: CreateMealRequest,
    ): Response<CreateMealResponse>

    @GET("api/meals/by-date")
    suspend fun listMealsByDate(
        @Query("user_id") userId: Long,
        @Query("date") date: String,
    ): Response<List<MealResponse>>

    @PUT("api/meals/{id}")
    suspend fun updateMeal(
        @Path("id") id: Long,
        @Body request: UpdateMealRequest,
    ): Response<UpdateMealResponse>

    @DELETE("api/meals/{id}")
    suspend fun deleteMeal(
        @Path("id") id: Long,
    ): Response<DeleteMealResponse>

    @GET("api/streaks/{user_id}")
    suspend fun getStreaks(
        @Path("user_id") userId: Long,
    ): Response<StreaksResponse>

    @GET("api/recipes")
    suspend fun listRecipes(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<List<RecipeResponse>>

    @POST("api/ratings")
    suspend fun createRating(
        @Body request: CreateRatingRequest,
    ): Response<CreateRatingResponse>

    @GET("api/metrics")
    suspend fun getMetrics(
        @Query("user_id") userId: Long,
    ): Response<MetricsResponse>

    @POST("api/metrics")
    suspend fun postMetrics(
        @Body request: MetricsRequest,
    ): Response<MetricsResponse>
}
