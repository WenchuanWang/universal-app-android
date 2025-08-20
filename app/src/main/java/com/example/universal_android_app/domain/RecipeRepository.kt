package com.example.universal_android_app.domain

interface RecipeRepository {
    suspend fun getRecipes(): Result<List<Recipe>>
    fun searchRecipes(query: String, recipes: List<Recipe>): List<Recipe>
}
