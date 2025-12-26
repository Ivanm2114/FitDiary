package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.MealResponse
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DiaryUiState(
    val isLoading: Boolean = false,
    val meals: List<MealResponse> = emptyList(),
    val errorMessage: String? = null,
)

class DiaryViewModel : ViewModel() {
    var uiState by mutableStateOf(DiaryUiState())
        private set

    fun loadMeals(
        userId: Long,
        date: LocalDate,
    ) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = ApiClient.api.listMealsByDate(
                    userId = userId,
                    date = dateString
                )
                if (response.isSuccessful) {
                    uiState = DiaryUiState(
                        isLoading = false,
                        meals = response.body().orEmpty(),
                        errorMessage = null
                    )
                } else {
                    uiState = DiaryUiState(
                        isLoading = false,
                        meals = emptyList(),
                        errorMessage = "Failed to load meals (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = DiaryUiState(
                    isLoading = false,
                    meals = emptyList(),
                    errorMessage = "Network error"
                )
            }
        }
    }
}

class DiaryViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            return DiaryViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
