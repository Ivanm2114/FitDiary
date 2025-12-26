package com.example.fitdiary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
) {
    data object Auth : AppDestination(
        route = "auth",
        label = "Auth",
        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Auth") }
    )


    data object Recipes : AppDestination(
        route = "recipes",
        label = "Recipes",
        icon = { Icon(Icons.Default.Menu, contentDescription = "Recipes") }
    )

    data object Diary : AppDestination(
        route = "diary",
        label = "Diary",
        icon = { Icon(Icons.Default.DateRange, contentDescription = "Diary") }
    )

    data object Profile : AppDestination(
        route = "profile",
        label = "Profile",
        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Profile") }
    )

    companion object {
        val topLevel: List<AppDestination> = listOf(Recipes, Diary, Profile)
    }
}

@Composable
fun AppDestination?.Icon() {
    if (this == null) {
        return
    }
    icon()
}

@Composable
fun AppDestination?.Label() {
    if (this == null) {
        return
    }
    Text(text = label)
}

fun AppDestination?.isSelected(currentRoute: String?): Boolean {
    if (this == null) {
        return false
    }
    return when (this) {
        is AppDestination.Recipes -> {
            currentRoute == route || currentRoute == AppRoutes.RECIPE_DETAIL
        }

        is AppDestination.Diary -> {
            currentRoute == route || currentRoute?.startsWith(AppRoutes.DIARY_ADD) == true
        }

        else -> currentRoute == route
    }
}

fun AppDestination?.navigate(navController: NavController) {
    if (this == null) {
        return
    }
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
