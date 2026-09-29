package com.orbit.recovery

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.orbit.recovery.ui.components.OrbitTab
import com.orbit.recovery.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) }
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToQuiz = {
                    navController.navigate("personalization_quiz")
                }
            )
        }
        composable("personalization_quiz") {
            PersonalizationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { navController.navigate("personalization_edu") },
                onSkip = { navController.navigate("personalization_edu") }
            )
        }
        composable("personalization_edu") {
            PersonalizationInsightsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { navController.navigate("results") },
                onSkip = { navController.navigate("results") }
            )
        }
        composable("results") {
            ResultsScreen(
                onNavigateToHome = { 
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        val onNavigateTab: (OrbitTab) -> Unit = { tab ->
            val route = when (tab) {
                OrbitTab.Home -> "home"
                OrbitTab.Garden -> "garden"
                OrbitTab.Coach -> "coach"
                OrbitTab.Learn -> "learn"
                OrbitTab.ScreenTime -> "screen_time"
            }
            navController.navigate(route) {
                popUpTo("home") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        composable("home") {
            HomeScreen(
                onNavigateTab = onNavigateTab,
                onNavigateToPanic = { navController.navigate("panic") },
                onNavigateToDailyCheckin = { navController.navigate("daily") },
                onNavigateToContentFilter = { navController.navigate("content_filter") },
                onNavigateToWelcome = { navController.navigate("welcome") },
                onNavigateToGarden = { navController.navigate("garden") },
                onNavigateToCoach = { navController.navigate("coach") },
                onNavigateToLearn = { navController.navigate("learn") },
                onNavigateToScreenTime = { navController.navigate("screen_time") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("coach") {
            CoachScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("daily") {
            DailyCheckinScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("panic") {
            PanicScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCoach = { navController.navigate("coach") }
            )
        }
        composable("learn") {
            LearnScreen(
                onNavigateTab = onNavigateTab,
                onNavigateToLesson = { id -> navController.navigate("learn/$id") }
            )
        }
        composable(
            route = "learn/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(
                lessonId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("garden") {
            GardenScreen(
                onNavigateTab = onNavigateTab,
                onNavigateToCheckin = { navController.navigate("daily") }
            )
        }
        composable("screen_time") {
            ScreenTimeScreen(
                onNavigateTab = onNavigateTab,
                onNavigateToContentFilter = { navController.navigate("content_filter") }
            )
        }
        composable("content_filter") {
            ContentFilterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWelcome = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToContentFilter = { navController.navigate("content_filter") },
                onNavigateToScreenTime = { navController.navigate("screen_time") }
            )
        }
    }
}
