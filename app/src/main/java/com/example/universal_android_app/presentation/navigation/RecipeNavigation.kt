package com.example.universal_android_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.universal_android_app.presentation.ui.screens.RecipeDetailScreen
import com.example.universal_android_app.presentation.ui.screens.RecipeListScreen
import com.example.universal_android_app.presentation.RecipeViewModel

sealed class Screen(val route: String) {
    data object RecipeGraph : Screen("recipe_graph")
    data object RecipeList : Screen("recipe_list")
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipe_detail/$recipeId"
    }
}

@Composable
fun RecipeNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RecipeGraph.route
    ) {
        navigation(
            startDestination = Screen.RecipeList.route,
            route = Screen.RecipeGraph.route
        ) {
            composable(Screen.RecipeList.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.RecipeGraph.route)
                }
                val viewModel: RecipeViewModel = hiltViewModel(parentEntry)

                RecipeListScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    viewModel = viewModel
                )
            }

            composable(Screen.RecipeDetail.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.RecipeGraph.route)
                }
                val viewModel: RecipeViewModel = hiltViewModel(parentEntry)

                val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull() ?: 0
                RecipeDetailScreen(
                    recipeId = recipeId,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
