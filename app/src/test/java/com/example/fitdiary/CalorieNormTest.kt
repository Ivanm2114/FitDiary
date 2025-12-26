package com.example.fitdiary

import com.example.fitdiary.domain.calculateCalorieNorm
import org.junit.Assert.assertEquals
import org.junit.Test

class CalorieNormTest {
    @Test
    fun maleMaintainUsesTdee() {
        val result = calculateCalorieNorm(
            weightKg = 80.0,
            heightCm = 180,
            age = 30,
            sex = "male",
            goal = "maintain"
        )
        assertEquals(2448, result)
    }

    @Test
    fun femaleLoseWeightSubtracts500() {
        val result = calculateCalorieNorm(
            weightKg = 60.0,
            heightCm = 165,
            age = 28,
            sex = "female",
            goal = "lose"
        )
        assertEquals(1329, result)
    }

    @Test
    fun gainWeightAdds500() {
        val result = calculateCalorieNorm(
            weightKg = 70.0,
            heightCm = 175,
            age = 25,
            sex = "male",
            goal = "gain"
        )
        assertEquals(2801, result)
    }
}
