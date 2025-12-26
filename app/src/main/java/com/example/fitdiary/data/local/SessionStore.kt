package com.example.fitdiary.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserSession(
    val id: Long,
    val nickname: String,
    val heightCm: Int?,
    val weightKg: Double?,
    val goal: String?,
    val age: Int?,
    val sex: String?,
)

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(
    private val context: Context,
) {
    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { preferences ->
        val id = preferences[KEY_USER_ID]
        val nickname = preferences[KEY_NICKNAME]
        if (id != null && nickname != null) {
            val heightCm = preferences[KEY_HEIGHT_CM]
            val weightKg = preferences[KEY_WEIGHT_KG]?.toDoubleOrNull()
            val goal = preferences[KEY_GOAL]
            val age = preferences[KEY_AGE]
            val sex = preferences[KEY_SEX]
            UserSession(
                id = id,
                nickname = nickname,
                heightCm = heightCm,
                weightKg = weightKg,
                goal = goal,
                age = age,
                sex = sex
            )
        } else {
            null
        }
    }

    suspend fun saveSession(
        id: Long,
        nickname: String,
        heightCm: Int? = null,
        weightKg: Double? = null,
        goal: String? = null,
        age: Int? = null,
        sex: String? = null,
        clearOptional: Boolean = false,
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = id
            preferences[KEY_NICKNAME] = nickname
            if (heightCm != null) {
                preferences[KEY_HEIGHT_CM] = heightCm
            } else if (clearOptional) {
                preferences.remove(KEY_HEIGHT_CM)
            }
            if (weightKg != null) {
                preferences[KEY_WEIGHT_KG] = weightKg.toString()
            } else if (clearOptional) {
                preferences.remove(KEY_WEIGHT_KG)
            }
            if (goal != null) {
                preferences[KEY_GOAL] = goal
            } else if (clearOptional) {
                preferences.remove(KEY_GOAL)
            }
            if (age != null) {
                preferences[KEY_AGE] = age
            } else if (clearOptional) {
                preferences.remove(KEY_AGE)
            }
            if (sex != null) {
                preferences[KEY_SEX] = sex
            } else if (clearOptional) {
                preferences.remove(KEY_SEX)
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_NICKNAME)
            preferences.remove(KEY_HEIGHT_CM)
            preferences.remove(KEY_WEIGHT_KG)
            preferences.remove(KEY_GOAL)
            preferences.remove(KEY_AGE)
            preferences.remove(KEY_SEX)
        }
    }

    private companion object {
        val KEY_USER_ID = longPreferencesKey("user_id")
        val KEY_NICKNAME = stringPreferencesKey("nickname")
        val KEY_HEIGHT_CM = intPreferencesKey("height_cm")
        val KEY_WEIGHT_KG = stringPreferencesKey("weight_kg")
        val KEY_GOAL = stringPreferencesKey("goal")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_SEX = stringPreferencesKey("sex")
    }
}
