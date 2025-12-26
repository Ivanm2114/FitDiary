package com.example.fitdiary.ui.screens.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.RecipeResponse
import com.example.fitdiary.ui.viewmodel.RecipesViewModel
import com.example.fitdiary.ui.viewmodel.RecipesViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun RecipesScreen(
    onOpenRecipe: (RecipeResponse) -> Unit,
) {
    val recipesViewModel: RecipesViewModel = viewModel(
        factory = RecipesViewModelFactory()
    )
    val uiState = recipesViewModel.uiState
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 2
        }
    }

    LaunchedEffect(Unit) {
        recipesViewModel.loadRecipes()
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            recipesViewModel.loadMore()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            delay(3000)
            recipesViewModel.loadRecipes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Recipes")
        Spacer(modifier = Modifier.height(12.dp))
        if (uiState.isLoading || (uiState.errorMessage != null && uiState.recipes.isEmpty())) {
            CircularProgressIndicator()
            return@Column
        }
        if (uiState.recipes.isEmpty()) {
            Text(text = "No recipes")
            return@Column
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(uiState.recipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onOpenRecipe(recipe) }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        AsyncImage(
                            model = buildImageUrl(recipe.imageUrl),
                            contentDescription = recipe.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = recipe.name)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${recipe.calories} kcal")
                            AssistChip(
                                onClick = { },
                                label = { Text(text = recipe.mealType) }
                            )
                        }
                    }
                }
            }
            if (uiState.isLoadingMore) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun buildImageUrl(imagePath: String?): String? {
    if (imagePath.isNullOrBlank()) {
        return null
    }
    return if (imagePath.startsWith("http")) {
        imagePath
    } else {
        "${ApiClient.baseUrl}$imagePath"
    }
}
