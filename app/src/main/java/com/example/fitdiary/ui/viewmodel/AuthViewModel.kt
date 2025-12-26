package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.CreateUserRequest
import com.example.fitdiary.data.api.LoginRequest
import com.example.fitdiary.data.local.SessionStore
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val sessionStore: SessionStore,
) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(
        login: String,
        password: String,
        heightText: String,
        weightText: String,
        goal: String,
        ageText: String,
        sex: String,
        onSuccess: () -> Unit,
    ) {
        val heightCm = heightText.toIntOrNull()
        val weightKg = weightText.toDoubleOrNull()
        val age = ageText.toIntOrNull()
        val sexApi = mapSexToApi(sex)
        if (login.isBlank() || password.isBlank()) {
            uiState = AuthUiState(errorMessage = "Login and password required")
            return
        }
        if (heightCm == null || heightCm <= 0) {
            uiState = AuthUiState(errorMessage = "Enter valid height")
            return
        }
        if (weightKg == null || weightKg <= 0.0) {
            uiState = AuthUiState(errorMessage = "Enter valid weight")
            return
        }
        if (goal.isBlank()) {
            uiState = AuthUiState(errorMessage = "Goal required")
            return
        }
        if (age == null || age <= 0) {
            uiState = AuthUiState(errorMessage = "Enter valid age")
            return
        }
        if (sexApi.isBlank()) {
            uiState = AuthUiState(errorMessage = "Sex required")
            return
        }

        uiState = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.register(
                    CreateUserRequest(
                        nickname = login,
                        password = password,
                        heightCm = heightCm,
                        weightKg = weightKg,
                        goal = goal,
                        age = age,
                        sex = sexApi,
                    )
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sessionStore.saveSession(
                        id = body.id,
                        nickname = body.nickname,
                        heightCm = heightCm,
                        weightKg = weightKg,
                        goal = goal,
                        age = age,
                        sex = sexApi,
                    )
                    uiState = AuthUiState()
                    onSuccess()
                } else {
                    uiState = AuthUiState(
                        errorMessage = "Registration failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = AuthUiState(errorMessage = "Network error")
            }
        }
    }

    fun login(
        login: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        if (login.isBlank() || password.isBlank()) {
            uiState = AuthUiState(errorMessage = "Login and password required")
            return
        }

        uiState = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.login(
                    LoginRequest(
                        nickname = login,
                        password = password,
                    )
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sessionStore.saveSession(body.id, body.nickname)
                    uiState = AuthUiState()
                    onSuccess()
                } else {
                    uiState = AuthUiState(
                        errorMessage = "Login failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = AuthUiState(errorMessage = "Network error")
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

class AuthViewModelFactory(
    private val sessionStore: SessionStore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(sessionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
