package com.example.fitdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.data.local.UserSession
import com.example.fitdiary.ui.screens.auth.AuthScreen
import com.example.fitdiary.ui.screens.diary.DiaryScreen
import com.example.fitdiary.ui.screens.diary.AddMealScreen
import com.example.fitdiary.ui.screens.diary.EditMealScreen
import com.example.fitdiary.ui.screens.profile.ProfileScreen
import com.example.fitdiary.ui.screens.profile.MetricsScreen
import com.example.fitdiary.ui.screens.recipes.RecipeDetailScreen
import com.example.fitdiary.ui.screens.recipes.RecipesScreen
import com.example.fitdiary.ui.screens.welcome.WelcomeScreen

object AppRoutes {
    const val AUTH = "auth"
    const val WELCOME = "welcome"
    const val DIARY_ADD = "diary/add"
    const val DIARY_ADD_WITH_DATE = "diary/add?date={date}"
    const val MEAL_EDIT = "meal/edit"
    const val METRICS = "profile/metrics"
    const val RECIPE_DETAIL = "recipe/{recipeId}"
    const val RECIPE_DETAIL_BASE = "recipe"

    fun diaryAddRoute(date: String?): String {
        return if (date.isNullOrBlank()) {
            DIARY_ADD
        } else {
            "diary/add?date=$date"
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onLoggedIn: () -> Unit,
    sessionStore: SessionStore,
    session: UserSession?,
) {
    val startDestination = if (isLoggedIn) {
        AppRoutes.WELCOME
    } else {
        AppRoutes.AUTH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.AUTH) {
            AuthScreen(
                sessionStore = sessionStore,
                onLogin = {
                    onLoggedIn()
                    navController.navigate(AppRoutes.WELCOME) {
                        popUpTo(AppRoutes.AUTH) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppRoutes.WELCOME) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(AppDestination.Diary.route) {
                        popUpTo(AppRoutes.WELCOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppDestination.Diary.route) {
            DiaryScreen(
                session = session,
                onAddMeal = { date ->
                    navController.navigate(AppRoutes.diaryAddRoute(date))
                },
                onEditMeal = { meal ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal_id", meal.id)
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal_name", meal.dishName)
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal_calories", meal.calories)
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal_date", meal.mealDate)
                    navController.navigate(AppRoutes.MEAL_EDIT)
                }
            )
        }
        composable(
            route = AppRoutes.DIARY_ADD_WITH_DATE,
            arguments = listOf(
                navArgument("date") {
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            AddMealScreen(
                session = session,
                selectedDate = date,
                onCreated = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppRoutes.MEAL_EDIT) {
            val handle = navController.previousBackStackEntry?.savedStateHandle
            val mealId = handle?.get<Long>("meal_id")
            val mealName = handle?.get<String>("meal_name")
            val mealCalories = handle?.get<Int>("meal_calories")
            val mealDate = handle?.get<String>("meal_date")
            val meal = if (mealId != null && mealName != null && mealCalories != null && mealDate != null) {
                com.example.fitdiary.data.api.MealResponse(
                    id = mealId,
                    userId = session?.id ?: 0,
                    dishName = mealName,
                    calories = mealCalories,
                    mealDate = mealDate
                )
            } else {
                null
            }
            EditMealScreen(
                meal = meal,
                onUpdated = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() }
            )
        }
        composable(AppDestination.Recipes.route) {
            RecipesScreen(
                onOpenRecipe = { recipe ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_id", recipe.id)
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_name", recipe.name)
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_type", recipe.mealType)
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_calories", recipe.calories)
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_text", recipe.recipeText)
                    navController.currentBackStackEntry?.savedStateHandle?.set("recipe_image", recipe.imageUrl)
                    navController.navigate("${AppRoutes.RECIPE_DETAIL_BASE}/${recipe.id}")
                }
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                session = session,
                sessionStore = sessionStore,
                onOpenMetrics = { navController.navigate(AppRoutes.METRICS) }
            )
        }
        composable(AppRoutes.METRICS) {
            MetricsScreen(
                session = session,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = AppRoutes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId").orEmpty()
            val handle = navController.previousBackStackEntry?.savedStateHandle
            val recipe = if (handle != null) {
                val id = handle.get<Long>("recipe_id")
                val name = handle.get<String>("recipe_name")
                val type = handle.get<String>("recipe_type")
                val calories = handle.get<Int>("recipe_calories")
                val text = handle.get<String>("recipe_text")
                val image = handle.get<String>("recipe_image")
                if (id != null && name != null && type != null && calories != null && text != null) {
                    com.example.fitdiary.data.api.RecipeResponse(
                        id = id,
                        name = name,
                        mealType = type,
                        calories = calories,
                        recipeText = text,
                        imageUrl = image
                    )
                } else {
                    null
                }
            } else {
                null
            }
            RecipeDetailScreen(
                recipeId = recipeId,
                recipe = recipe,
                onBack = {
                    val popped = navController.popBackStack(AppDestination.Recipes.route, false)
                    if (!popped) {
                        navController.navigate(AppDestination.Recipes.route) {
                            popUpTo(AppDestination.Recipes.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
