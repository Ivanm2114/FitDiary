package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.CreateMealRequest
import com.example.fitdiary.data.api.CreateRatingRequest
import kotlinx.coroutines.launch

data class AddMealUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class AddMealViewModel : ViewModel() {
    var uiState by mutableStateOf(AddMealUiState())
        private set

    fun createMeal(
        userId: Long,
        dishName: String,
        calories: Int,
        mealDate: String?,
        onSuccess: () -> Unit,
    ) {
        uiState = AddMealUiState(isSaving = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.createMeal(
                    CreateMealRequest(
                        userId = userId,
                        dishName = dishName,
                        calories = calories,
                        mealDate = mealDate
                    )
                )
                if (response.isSuccessful) {
                    uiState = AddMealUiState()
                    onSuccess()
                } else {
                    uiState = AddMealUiState(
                        errorMessage = "Create failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = AddMealUiState(errorMessage = "Network error")
            }
        }
    }

    fun createRating(
        rating: Int,
        onSuccess: () -> Unit,
    ) {
        uiState = AddMealUiState(isSaving = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.createRating(
                    CreateRatingRequest(rating = rating)
                )
                if (response.isSuccessful) {
                    uiState = AddMealUiState()
                    onSuccess()
                } else {
                    uiState = AddMealUiState(
                        errorMessage = "Rating failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = AddMealUiState(errorMessage = "Network error")
            }
        }
    }
}

class AddMealViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMealViewModel::class.java)) {
            return AddMealViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
