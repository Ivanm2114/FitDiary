package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.StreaksResponse
import com.example.fitdiary.data.api.UpdateUserRequest
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.data.local.UserSession
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val streaks: StreaksResponse? = null,
)

class ProfileViewModel(
    private val sessionStore: SessionStore,
) : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadStreaks(userId: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getStreaks(userId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(streaks = response.body())
                } else {
                    uiState = uiState.copy(
                        errorMessage = "Failed to load streaks (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = uiState.copy(errorMessage = "Network error")
            }
        }
    }

    fun updateProfile(
        session: UserSession,
        heightCm: Int,
        weightKg: Double,
        goal: String,
        age: Int,
        sex: String,
        onSuccess: () -> Unit,
    ) {
        val sexApi = mapSexToApi(sex)
        uiState = uiState.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.updateUser(
                    id = session.id,
                    request = UpdateUserRequest(
                        heightCm = heightCm,
                        weightKg = weightKg,
                        goal = goal,
                        age = age,
                        sex = sexApi
                    )
                )
                if (response.isSuccessful) {
                    sessionStore.saveSession(
                        id = session.id,
                        nickname = session.nickname,
                        heightCm = heightCm,
                        weightKg = weightKg,
                        goal = goal,
                        age = age,
                        sex = sexApi
                    )
                    uiState = uiState.copy(isSaving = false)
                    onSuccess()
                } else {
                    uiState = uiState.copy(
                        isSaving = false,
                        errorMessage = "Update failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = uiState.copy(isSaving = false, errorMessage = "Network error")
            }
        }
    }

    private fun mapSexToApi(value: String): String {
        return when (value.lowercase()) {
            "мужчина" -> "male"
            "женщина" -> "female"
            else -> value
        }
    }
}

class ProfileViewModelFactory(
    private val sessionStore: SessionStore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(sessionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
