package com.example.fitdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.metrics.MetricsTracker
import com.example.fitdiary.ui.theme.FitdiaryTheme
import com.example.fitdiary.ui.navigation.AppDestination
import com.example.fitdiary.ui.navigation.AppNavHost
import com.example.fitdiary.ui.navigation.Icon
import com.example.fitdiary.ui.navigation.Label
import com.example.fitdiary.ui.navigation.isSelected
import com.example.fitdiary.ui.navigation.navigate
import com.example.fitdiary.ui.viewmodel.MainViewModel
import com.example.fitdiary.ui.viewmodel.MainViewModelFactory
import com.example.fitdiary.ui.navigation.AppRoutes


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MetricsTracker.markAppStart()
        enableEdgeToEdge()
        setContent {
            FitdiaryTheme {
                FitdiaryApp()
                MetricsTracker.markProcessStart()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MetricsTracker.onSessionStart()
    }

    override fun onStop() {
        MetricsTracker.onSessionStop()
        super.onStop()
    }
}

@PreviewScreenSizes
@Composable
fun FitdiaryApp() {
    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context.applicationContext) }
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(sessionStore)
    )
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val selectedRoute = currentRoute
    val session = mainViewModel.sessionFlow.collectAsState(initial = null).value
    val isLoggedIn = session != null
    val showNavBar = isLoggedIn && currentRoute != AppRoutes.AUTH

    LaunchedEffect(isLoggedIn, currentRoute) {
        if (currentRoute == null) {
            val targetRoute = if (isLoggedIn) AppRoutes.WELCOME else AppRoutes.AUTH
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        if (isLoggedIn && currentRoute == AppRoutes.AUTH) {
            navController.navigate(AppRoutes.WELCOME) {
                popUpTo(AppRoutes.AUTH) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showNavBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { AppDestination.Recipes.Icon() },
                        label = { AppDestination.Recipes.Label() },
                        selected = AppDestination.Recipes.isSelected(selectedRoute),
                        onClick = {
                            AppDestination.Recipes.navigate(navController)
                        }
                    )
                    NavigationBarItem(
                        icon = { AppDestination.Diary.Icon() },
                        label = { AppDestination.Diary.Label() },
                        selected = AppDestination.Diary.isSelected(selectedRoute),
                        onClick = {
                            AppDestination.Diary.navigate(navController)
                        }
                    )
                    NavigationBarItem(
                        icon = { AppDestination.Profile.Icon() },
                        label = { AppDestination.Profile.Label() },
                        selected = AppDestination.Profile.isSelected(selectedRoute),
                        onClick = {
                            AppDestination.Profile.navigate(navController)
                        }
                    )
                }
            }
        },
        floatingActionButton = { }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavHost(
                navController = navController,
                isLoggedIn = isLoggedIn,
                onLoggedIn = { },
                sessionStore = sessionStore,
                session = session
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitdiaryTheme {
        FitdiaryApp()
    }
}
