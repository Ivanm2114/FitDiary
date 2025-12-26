package com.example.fitdiary.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitdiary.data.api.MetricsRequest
import com.example.fitdiary.data.local.UserSession
import com.example.fitdiary.metrics.MetricsTracker
import com.example.fitdiary.ui.viewmodel.MetricsViewModel
import com.example.fitdiary.ui.viewmodel.MetricsViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun MetricsScreen(
    session: UserSession?,
    onBack: () -> Unit,
) {
    val metricsViewModel: MetricsViewModel = viewModel(
        factory = MetricsViewModelFactory()
    )
    val uiState = metricsViewModel.uiState
    var metrics by remember { mutableStateOf(MetricsTracker.snapshot()) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            metrics = MetricsTracker.snapshot()
            delay(1000)
        }
    }
    LaunchedEffect(session?.id) {
        if (session == null) {
            return@LaunchedEffect
        }
        while (true) {
            delay(60_000)
            metricsViewModel.sendMetrics(
                request = MetricsRequest(
                    userId = session.id,
                    crashRate = metrics.crashRate,
                    startTime = metrics.startTime,
                    retention = metrics.retention,
                    sessionLength = metrics.sessionLength,
                    errorRate = metrics.errorRate,
                    totalRequests = metrics.totalRequests
                ),
                onSuccess = { }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Metrics")
            Button(onClick = onBack) {
                Text(text = "Back")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Crash rate: ${metrics.crashRate}")
        Text(text = "Session length: ${metrics.sessionLength}")
        Text(text = "Error rate: ${metrics.errorRate}")
        Text(text = "Total requests: ${metrics.totalRequests}")
        Text(text = "Error requests: ${metrics.errorRequests}")
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.errorMessage)
        }
        if (errorText != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorText.orEmpty())
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (session == null) {
                    errorText = "User required"
                    return@Button
                }
                errorText = null
                val snapshot = MetricsTracker.snapshot()
                metricsViewModel.sendMetrics(
                    request = MetricsRequest(
                        userId = session.id,
                        crashRate = snapshot.crashRate,
                        startTime = snapshot.startTime,
                        retention = snapshot.retention,
                        sessionLength = snapshot.sessionLength,
                        errorRate = snapshot.errorRate,
                        totalRequests = snapshot.totalRequests
                    ),
                    onSuccess = { }
                )
            },
            enabled = !uiState.isSending
        ) {
            Text(text = if (uiState.isSending) "Sending..." else "Send")
        }
    }
}
