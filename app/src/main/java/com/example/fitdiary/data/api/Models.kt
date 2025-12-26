package com.example.fitdiary.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val nickname: String,
    val password: String,
    @SerialName("height_cm")
    val heightCm: Int,
    @SerialName("weight_kg")
    val weightKg: Double,
    val goal: String,
    val age: Int,
    val sex: String,
)

@Serializable
data class LoginRequest(
    val nickname: String,
    val password: String,
)

@Serializable
data class CreateUserResponse(
    val id: Long,
    val nickname: String,
)

@Serializable
data class UpdateUserRequest(
    @SerialName("height_cm")
    val heightCm: Int? = null,
    @SerialName("weight_kg")
    val weightKg: Double? = null,
    val goal: String? = null,
    val age: Int? = null,
    val sex: String? = null,
)

@Serializable
data class UpdateUserResponse(
    val status: String,
)

@Serializable
data class CreateMealRequest(
    @SerialName("user_id")
    val userId: Long,
    @SerialName("dish_name")
    val dishName: String,
    val calories: Int,
    @SerialName("meal_date")
    val mealDate: String? = null,
)

@Serializable
data class CreateMealResponse(
    val status: String,
)

@Serializable
data class MealResponse(
    val id: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("dish_name")
    val dishName: String,
    val calories: Int,
    @SerialName("meal_date")
    val mealDate: String,
)

@Serializable
data class UpdateMealRequest(
    @SerialName("dish_name")
    val dishName: String? = null,
    val calories: Int? = null,
    @SerialName("meal_date")
    val mealDate: String? = null,
)

@Serializable
data class UpdateMealResponse(
    val status: String,
)

@Serializable
data class DeleteMealResponse(
    val status: String,
)

@Serializable
data class StreaksResponse(
    @SerialName("user_id")
    val userId: Long,
    @SerialName("current_streak")
    val currentStreak: Int,
    @SerialName("max_streak")
    val maxStreak: Int,
)

@Serializable
data class RecipeResponse(
    val id: Long,
    val name: String,
    @SerialName("meal_type")
    val mealType: String,
    val calories: Int,
    @SerialName("recipe_text")
    val recipeText: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
)

@Serializable
data class CreateRatingRequest(
    val rating: Int,
)

@Serializable
data class CreateRatingResponse(
    val status: String,
)

@Serializable
data class MetricsRequest(
    @SerialName("user_id")
    val userId: Long,
    @SerialName("crash_rate")
    val crashRate: Double,
    @SerialName("start_time")
    val startTime: Double,
    val retention: Double,
    @SerialName("session_length")
    val sessionLength: Double,
    @SerialName("error_rate")
    val errorRate: Double,
    @SerialName("total_requests")
    val totalRequests: Long,
)

@Serializable
data class MetricsResponse(
    @SerialName("user_id")
    val userId: Long,
    @SerialName("crash_rate")
    val crashRate: Double,
    @SerialName("start_time")
    val startTime: Double,
    val retention: Double,
    @SerialName("session_length")
    val sessionLength: Double,
    @SerialName("error_rate")
    val errorRate: Double,
    @SerialName("total_requests")
    val totalRequests: Long,
)
