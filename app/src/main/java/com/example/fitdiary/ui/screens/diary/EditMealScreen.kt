package com.example.fitdiary.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitdiary.data.api.MealResponse
import com.example.fitdiary.ui.viewmodel.EditMealViewModel
import com.example.fitdiary.ui.viewmodel.EditMealViewModelFactory

@Composable
fun EditMealScreen(
    meal: MealResponse?,
    onUpdated: () -> Unit,
    onDeleted: () -> Unit,
) {
    val editMealViewModel: EditMealViewModel = viewModel(
        factory = EditMealViewModelFactory()
    )
    val uiState = editMealViewModel.uiState
    if (meal == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Meal not found")
        }
        return
    }
    var dishName by rememberSaveable { mutableStateOf(meal.dishName) }
    var caloriesText by rememberSaveable { mutableStateOf(meal.calories.toString()) }
    var dateText by rememberSaveable { mutableStateOf(meal.mealDate) }
    val canSubmit = dishName.isNotBlank() && caloriesText.isNotBlank() && !uiState.isSaving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit meal")
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
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text(text = "Date (YYYY-MM-DD)") },
            singleLine = true
        )
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.errorMessage)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val calories = caloriesText.toIntOrNull()
                    if (calories == null) {
                        return@Button
                    }
                    editMealViewModel.updateMeal(
                        mealId = meal.id,
                        dishName = dishName,
                        calories = calories,
                        mealDate = dateText.ifBlank { null },
                        onSuccess = onUpdated
                    )
                },
                enabled = canSubmit
            ) {
                Text(text = if (uiState.isSaving) "Saving..." else "Save")
            }
            Button(
                onClick = {
                    editMealViewModel.deleteMeal(
                        mealId = meal.id,
                        onSuccess = onDeleted
                    )
                },
                enabled = !uiState.isSaving
            ) {
                Text(text = "Delete")
            }
        }
    }
}
