package com.example.fitdiary.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.data.local.UserSession
import com.example.fitdiary.ui.viewmodel.ProfileViewModel
import com.example.fitdiary.ui.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    session: UserSession?,
    sessionStore: SessionStore,
    onOpenMetrics: () -> Unit,
) {
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(sessionStore)
    )
    val uiState = profileViewModel.uiState
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var heightText by rememberSaveable { mutableStateOf("") }
    var weightText by rememberSaveable { mutableStateOf("") }
    var ageText by rememberSaveable { mutableStateOf("") }
    var goalText by rememberSaveable { mutableStateOf<String?>(null) }
    var goalExpanded by rememberSaveable { mutableStateOf(false) }
    var sexText by rememberSaveable { mutableStateOf<String?>(null) }
    var sexExpanded by rememberSaveable { mutableStateOf(false) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    fun openEdit() {
        if (session == null) {
            return
        }
        heightText = session.heightCm?.toString().orEmpty()
        weightText = session.weightKg?.toString().orEmpty()
        ageText = session.age?.toString().orEmpty()
        goalText = session.goal
        sexText = mapSexToDisplay(session.sex)
        errorText = null
        showEdit = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (session != null) {
                IconButton(onClick = { openEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile"
                    )
                }
            }
        }
        Text(text = "Profile")
        Spacer(modifier = Modifier.height(12.dp))
        if (session == null) {
            Text(text = "Not logged in")
        } else {
            LaunchedEffect(session.id) {
                profileViewModel.loadStreaks(session.id)
            }
            androidx.compose.runtime.DisposableEffect(lifecycleOwner, session.id) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        profileViewModel.loadStreaks(session.id)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            Text(text = "User ID: ${session.id}")
            Text(text = "Nickname: ${session.nickname}")
            Text(text = "Height: ${session.heightCm ?: "-"} cm")
            Text(text = "Weight: ${session.weightKg ?: "-"} kg")
            Text(text = "Age: ${session.age ?: "-"}")
            Text(text = "Sex: ${mapSexToDisplay(session.sex) ?: "-"}")
            Text(text = "Goal: ${session.goal ?: "-"}")
            Text(text = "Current streak: ${uiState.streaks?.currentStreak ?: "-"}")
            Text(text = "Max streak: ${uiState.streaks?.maxStreak ?: "-"}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        sessionStore.clear()
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Log out")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onOpenMetrics) {
                Text(text = "Metrics")
            }
        }
    }

    if (showEdit && session != null) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            title = { Text(text = "Edit profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text(text = "Height (cm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text(text = "Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text(text = "Age") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = sexExpanded,
                        onExpandedChange = { sexExpanded = !sexExpanded }
                    ) {
                        OutlinedTextField(
                            value = sexText.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Sex") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = sexExpanded,
                            onDismissRequest = { sexExpanded = false }
                        ) {
                            SEX_OPTIONS.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        sexText = option
                                        sexExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = goalExpanded,
                        onExpandedChange = { goalExpanded = !goalExpanded }
                    ) {
                        OutlinedTextField(
                            value = goalText.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Goal") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = goalExpanded,
                            onDismissRequest = { goalExpanded = false }
                        ) {
                            GOAL_OPTIONS.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        goalText = option
                                        goalExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (errorText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorText.orEmpty())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val height = heightText.toIntOrNull()
                        val weight = weightText.toDoubleOrNull()
                        val age = ageText.toIntOrNull()
                        if (height == null || height <= 0) {
                            errorText = "Enter valid height"
                            return@Button
                        }
                        if (weight == null || weight <= 0.0) {
                            errorText = "Enter valid weight"
                            return@Button
                        }
                        if (age == null || age <= 0) {
                            errorText = "Enter valid age"
                            return@Button
                        }
                        if (sexText.isNullOrBlank()) {
                            errorText = "Sex required"
                            return@Button
                        }
                        if (goalText.isNullOrBlank()) {
                            errorText = "Goal required"
                            return@Button
                        }
                        profileViewModel.updateProfile(
                            session = session,
                            heightCm = height,
                            weightKg = weight,
                            goal = goalText.orEmpty(),
                            age = age,
                            sex = sexText.orEmpty(),
                            onSuccess = { showEdit = false }
                        )
                    }
                ) {
                    Text(text = if (uiState.isSaving) "Saving..." else "Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEdit = false },
                    enabled = !uiState.isSaving
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

private val GOAL_OPTIONS = listOf(
    "похудеть",
    "поддерживать",
    "набрать массу"
)

private val SEX_OPTIONS = listOf(
    "Мужчина",
    "Женщина"
)

private fun mapSexToDisplay(value: String?): String? {
    return when (value?.lowercase()) {
        "male" -> "Мужчина"
        "female" -> "Женщина"
        else -> value
    }
}
