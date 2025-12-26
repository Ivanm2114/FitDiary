package com.example.fitdiary.domain

import kotlin.math.roundToInt

fun calculateCalorieNorm(
    weightKg: Double,
    heightCm: Int,
    age: Int,
    sex: String,
    goal: String,
): Int {
    val isMale = sex.equals("male", ignoreCase = true) || sex.equals("Мужчина", ignoreCase = true)
    val bmr = if (isMale) {
        10.0 * weightKg + 6.25 * heightCm - 5.0 * age + 5.0
    } else {
        10.0 * weightKg + 6.25 * heightCm - 5.0 * age - 161.0
    }
    val tdee = bmr * 1.375
    val goalLower = goal.lowercase()
    val adjusted = when {
        goalLower.contains("похуд") || goalLower.contains("lose") -> tdee - 500.0
        goalLower.contains("набрать") || goalLower.contains("gain") -> tdee + 500.0
        else -> tdee
    }
    return adjusted.roundToInt()
}
