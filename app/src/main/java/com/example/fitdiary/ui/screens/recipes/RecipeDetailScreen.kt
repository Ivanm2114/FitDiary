package com.example.fitdiary.ui.screens.recipes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fitdiary.data.api.ApiClient
import com.example.fitdiary.data.api.RecipeResponse

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    recipe: RecipeResponse?,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(text = "Recipe")
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (recipe == null) {
            Text(text = "Recipe not found (id: $recipeId)")
            return@Column
        }
        AsyncImage(
            model = buildImageUrl(recipe.imageUrl),
            contentDescription = recipe.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = recipe.name)
        Text(text = "Type: ${recipe.mealType}")
        Text(text = "Calories: ${recipe.calories} kcal")
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = recipe.recipeText)
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
