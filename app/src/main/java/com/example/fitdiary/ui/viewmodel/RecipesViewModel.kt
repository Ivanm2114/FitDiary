package com.example.fitdiary.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.RecipeResponse
import kotlinx.coroutines.launch

data class RecipesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val recipes: List<RecipeResponse> = emptyList(),
    val errorMessage: String? = null,
    val page: Int = 1,
    val canLoadMore: Boolean = true,
)

class RecipesViewModel : ViewModel() {
    var uiState by mutableStateOf(RecipesUiState())
        private set

    fun loadRecipes() {
        uiState = uiState.copy(isLoading = true, errorMessage = null, page = 1, canLoadMore = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.listRecipes(
                    page = 1,
                    limit = PAGE_SIZE
                )
                if (response.isSuccessful) {
                    val items = response.body().orEmpty()
                    uiState = RecipesUiState(
                        isLoading = false,
                        recipes = items,
                        errorMessage = null,
                        page = 1,
                        canLoadMore = items.size == PAGE_SIZE
                    )
                } else {
                    uiState = RecipesUiState(
                        isLoading = false,
                        recipes = emptyList(),
                        errorMessage = "Failed to load recipes (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = RecipesUiState(
                    isLoading = false,
                    recipes = emptyList(),
                    errorMessage = "Network error"
                )
            }
        }
    }

    fun loadMore() {
        if (uiState.isLoading || uiState.isLoadingMore || !uiState.canLoadMore) {
            return
        }
        val nextPage = uiState.page + 1
        uiState = uiState.copy(isLoadingMore = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val response = ApiClient.api.listRecipes(
                    page = nextPage,
                    limit = PAGE_SIZE
                )
                if (response.isSuccessful) {
                    val items = response.body().orEmpty()
                    uiState = uiState.copy(
                        isLoadingMore = false,
                        recipes = uiState.recipes + items,
                        page = nextPage,
                        canLoadMore = items.size == PAGE_SIZE
                    )
                } else {
                    uiState = uiState.copy(
                        isLoadingMore = false,
                        errorMessage = "Failed to load recipes (${response.code()})"
                    )
                }
            } catch (exception: Exception) {
                uiState = uiState.copy(isLoadingMore = false, errorMessage = "Network error")
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}

class RecipesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesViewModel::class.java)) {
            return RecipesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
