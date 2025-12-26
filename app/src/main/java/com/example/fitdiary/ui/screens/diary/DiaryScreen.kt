package com.example.fitdiary.ui.screens.diary

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitdiary.data.local.UserSession
import com.example.fitdiary.data.api.MealResponse
import com.example.fitdiary.ui.viewmodel.DiaryViewModel
import com.example.fitdiary.ui.viewmodel.DiaryViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.fitdiary.domain.calculateCalorieNorm

@Composable
fun DiaryScreen(
    session: UserSession?,
    onAddMeal: (String?) -> Unit,
    onEditMeal: (MealResponse) -> Unit,
) {
    val context = LocalContext.current
    val diaryViewModel: DiaryViewModel = viewModel(
        factory = DiaryViewModelFactory()
    )
    val uiState = diaryViewModel.uiState
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(session?.id, selectedDate) {
        if (session != null) {
            diaryViewModel.loadMeals(
                userId = session.id,
                date = selectedDate
            )
        }
    }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner, session?.id) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && session != null) {
                diaryViewModel.loadMeals(
                    userId = session.id,
                    date = selectedDate
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val displayDate = remember(selectedDate) {
            selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val initial = selectedDate
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        },
                        initial.year,
                        initial.monthValue - 1,
                        initial.dayOfMonth
                    ).show()
                }
            ) {
                Text(text = displayDate)
            }
            Button(
                onClick = {
                    val date = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    onAddMeal(date)
                },
                enabled = session != null
            ) {
                Text(text = "Add meal")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        val eatenCalories = uiState.meals.sumOf { it.calories }
        val normCalories = calculateCalorieNormOrNull(session)
        Text(text = "Eaten: $eatenCalories kcal")
        Text(text = "Norm: ${normCalories ?: "-"} kcal")
        if (normCalories != null && normCalories > 0) {
            val progress = (eatenCalories.toFloat() / normCalories.toFloat()).coerceIn(0f, 1f)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (session == null) {
            Text(text = "Not logged in")
            return@Column
        }
        if (uiState.isLoading) {
            Text(text = "Loading...")
            return@Column
        }
        if (uiState.errorMessage != null) {
            Text(text = uiState.errorMessage)
            return@Column
        }
        if (uiState.meals.isEmpty()) {
            Text(text = "No meals for this date")
            return@Column
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.meals) { meal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onEditMeal(meal) }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = meal.dishName)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${meal.calories} kcal")
                    }
                }
            }
        }
    }
}

private fun calculateCalorieNormOrNull(session: UserSession?): Int? {
    if (session == null) {
        return null
    }
    val weightKg = session.weightKg
    val heightCm = session.heightCm
    val age = session.age
    val sex = session.sex
    val goal = session.goal
    if (weightKg == null || heightCm == null || age == null || sex.isNullOrBlank() || goal.isNullOrBlank()) {
        return null
    }
    return calculateCalorieNorm(
        weightKg = weightKg,
        heightCm = heightCm,
        age = age,
        sex = sex,
        goal = goal
    )
}
