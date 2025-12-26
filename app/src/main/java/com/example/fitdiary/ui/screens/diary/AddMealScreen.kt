package com.example.fitdiary.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitdiary.data.local.UserSession
import com.example.fitdiary.ui.viewmodel.AddMealViewModel
import com.example.fitdiary.ui.viewmodel.AddMealViewModelFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star

@Composable
fun AddMealScreen(
    session: UserSession?,
    selectedDate: String?,
    onCreated: () -> Unit,
) {
    val addMealViewModel: AddMealViewModel = viewModel(
        factory = AddMealViewModelFactory()
    )
    val uiState = addMealViewModel.uiState
    var dishName by rememberSaveable { mutableStateOf("") }
    var caloriesText by rememberSaveable { mutableStateOf("") }
    var showRating by rememberSaveable { mutableStateOf(false) }
    var selectedRating by rememberSaveable { mutableStateOf(5) }
    val canSubmit = dishName.isNotBlank() && caloriesText.isNotBlank() && !uiState.isSaving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add meal")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = dishName,
            onValueChange = { dishName = it },
            label = { Text(text = "Dish name") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = caloriesText,
            onValueChange = { caloriesText = it },
            label = { Text(text = "Calories") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.errorMessage)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                val calories = caloriesText.toIntOrNull()
                if (session == null || calories == null) {
                    return@Button
                }
                addMealViewModel.createMeal(
                    userId = session.id,
                    dishName = dishName,
                    calories = calories,
                    mealDate = selectedDate,
                    onSuccess = { showRating = true }
                )
            },
            enabled = canSubmit && session != null
        ) {
            Text(text = if (uiState.isSaving) "Saving..." else "Submit")
        }
    }

    if (showRating) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Rate the app") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { rating ->
                        val isSelected = rating <= selectedRating
                        IconButton(
                            onClick = { selectedRating = rating },
                            enabled = !uiState.isSaving
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Rate $rating",
                                tint = if (isSelected) Color(0xFFFFC107) else Color.Gray
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        addMealViewModel.createRating(
                            rating = selectedRating,
                            onSuccess = onCreated
                        )
                    },
                    enabled = !uiState.isSaving
                ) {
                    Text(text = if (uiState.isSaving) "Sending..." else "Send")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCreated,
                    enabled = !uiState.isSaving
                ) {
                    Text(text = "Skip")
                }
            }
        )
    }
}
