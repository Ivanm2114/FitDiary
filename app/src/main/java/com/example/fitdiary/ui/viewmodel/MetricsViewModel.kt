package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.MetricsRequest
import com.example.fitdiary.data.api.MetricsResponse
import kotlinx.coroutines.launch

data class MetricsUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val metrics: MetricsResponse? = null,
    val errorMessage: String? = null,
)

class MetricsViewModel : ViewModel() {
    var uiState by mutableStateOf(MetricsUiState())
        private set

    fun loadMetrics(userId: Long) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getMetrics(userId = userId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        metrics = response.body(),
                        errorMessage = null
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Failed to load metrics (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Network error")
            }
        }
    }

    fun sendMetrics(
        request: MetricsRequest,
        onSuccess: () -> Unit,
    ) {
        uiState = uiState.copy(isSending = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.postMetrics(request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isSending = false,
                        metrics = response.body(),
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    uiState = uiState.copy(
                        isSending = false,
                        errorMessage = "Failed to send metrics (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = uiState.copy(isSending = false)
            }
        }
    }
}

class MetricsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MetricsViewModel::class.java)) {
            return MetricsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
