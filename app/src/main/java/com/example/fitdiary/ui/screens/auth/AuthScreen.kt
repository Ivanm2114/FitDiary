package com.example.fitdiary.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.ui.viewmodel.AuthViewModel
import com.example.fitdiary.ui.viewmodel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    sessionStore: SessionStore,
    onLogin: () -> Unit,
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(sessionStore)
    )
    val uiState = authViewModel.uiState
    var mode by remember { mutableStateOf<AuthMode?>(null) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf<String?>(null) }
    var goalExpanded by remember { mutableStateOf(false) }
    var sex by remember { mutableStateOf<String?>(null) }
    var sexExpanded by remember { mutableStateOf(false) }
    val canSubmit = when (mode) {
        AuthMode.REGISTER -> {
            login.isNotBlank() &&
                password.isNotBlank() &&
                heightCm.isNotBlank() &&
                weightKg.isNotBlank() &&
                ageText.isNotBlank() &&
                goal != null &&
                sex != null &&
                !uiState.isLoading
        }
        AuthMode.LOGIN -> {
            login.isNotBlank() &&
                password.isNotBlank() &&
                !uiState.isLoading
        }
        null -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Fitdiary")
        Spacer(modifier = Modifier.height(16.dp))
        if (mode == null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = { mode = AuthMode.REGISTER }) {
                    Text(text = "Register")
                }
                Button(onClick = { mode = AuthMode.LOGIN }) {
                    Text(text = "Login")
                }
            }
        } else {
            Text(text = if (mode == AuthMode.REGISTER) "Register" else "Login")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(text = "Login") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            if (mode == AuthMode.REGISTER) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = { Text(text = "Height (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    label = { Text(text = "Weight (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text(text = "Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = sexExpanded,
                    onExpandedChange = { sexExpanded = !sexExpanded }
                ) {
                    OutlinedTextField(
                        value = sex.orEmpty(),
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
                                    sex = option
                                    sexExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = goalExpanded,
                    onExpandedChange = { goalExpanded = !goalExpanded }
                ) {
                    OutlinedTextField(
                        value = goal.orEmpty(),
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
                                    goal = option
                                    goalExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = uiState.errorMessage)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        mode = null
                        login = ""
                        password = ""
                        heightCm = ""
                        weightKg = ""
                        ageText = ""
                        goal = null
                        sex = null
                    }
                ) {
                    Text(text = "Back")
                }
                Button(
                    onClick = {
                        if (mode == AuthMode.REGISTER) {
                            authViewModel.register(
                                login = login,
                                password = password,
                                heightText = heightCm,
                                weightText = weightKg,
                                goal = goal.orEmpty(),
                                ageText = ageText,
                                sex = sex.orEmpty(),
                                onSuccess = onLogin
                            )
                        } else {
                            authViewModel.login(login, password, onLogin)
                        }
                    },
                    enabled = canSubmit
                ) {
                    val label = if (mode == AuthMode.REGISTER) "Register" else "Login"
                    Text(text = if (uiState.isLoading) "Please wait..." else label)
                }
            }
        }
    }
}

private enum class AuthMode {
    REGISTER,
    LOGIN,
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
