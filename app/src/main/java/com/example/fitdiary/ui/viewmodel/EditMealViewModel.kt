package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.UpdateMealRequest
import kotlinx.coroutines.launch

data class EditMealUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class EditMealViewModel : ViewModel() {
    var uiState by mutableStateOf(EditMealUiState())
        private set

    fun updateMeal(
        mealId: Long,
        dishName: String,
        calories: Int,
        mealDate: String?,
        onSuccess: () -> Unit,
    ) {
        uiState = EditMealUiState(isSaving = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.updateMeal(
                    id = mealId,
                    request = UpdateMealRequest(
                        dishName = dishName,
                        calories = calories,
                        mealDate = mealDate
                    )
                )
                if (response.isSuccessful) {
                    uiState = EditMealUiState()
                    onSuccess()
                } else {
                    uiState = EditMealUiState(
                        errorMessage = "Update failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = EditMealUiState(errorMessage = "Network error")
            }
        }
    }

    fun deleteMeal(
        mealId: Long,
        onSuccess: () -> Unit,
    ) {
        uiState = EditMealUiState(isSaving = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.deleteMeal(mealId)
                if (response.isSuccessful) {
                    uiState = EditMealUiState()
                    onSuccess()
                } else {
                    uiState = EditMealUiState(
                        errorMessage = "Delete failed (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = EditMealUiState(errorMessage = "Network error")
            }
        }
    }
}

class EditMealViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditMealViewModel::class.java)) {
            return EditMealViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
