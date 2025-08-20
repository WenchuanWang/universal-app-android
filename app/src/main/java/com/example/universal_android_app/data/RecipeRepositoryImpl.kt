package com.example.universal_android_app.data

import android.content.Context
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.domain.RecipeRepository
import com.example.universal_android_app.data.remote.RecipeApiService
import com.example.universal_android_app.data.mapper.toRecipeList
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val apiService: RecipeApiService,
    @ApplicationContext private val context: Context,
    private val baseUrl: String
): RecipeRepository {
    
    override suspend fun getRecipes(): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            // Load from local JSON file
            val recipes = loadLocalRecipes()
            if (recipes.isNotEmpty()) {
                Result.success(recipes)
            } else {
                // Fallback to API call
                val response = apiService.getRecipes()
                Result.success(response.toRecipeList(baseUrl))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun loadLocalRecipes(): List<Recipe> {
        return try {
            context.assets.open("recipes.json").use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val response = Gson().fromJson(jsonString, RecipeListResponse::class.java)
                response.toRecipeList(baseUrl)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun searchRecipes(query: String, recipes: List<Recipe>): List<Recipe> {
        return recipes.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true) ||
            recipe.description.contains(query, ignoreCase = true) ||
            recipe.ingredients.any { ingredient ->
                ingredient.ingredient.contains(query, ignoreCase = true)
            }
        }
    }
}
