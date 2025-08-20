package com.example.universal_android_app.domain

data class Recipe(
    val title: String,
    val description: String,
    val imageUrl: String,
    val imageDescription: String,
    val recipeDetails: RecipeDetails,
    val ingredients: List<Ingredient>
) {
    data class RecipeDetails(
        val amountLabel: String,
        val amountNumber: Int,
        val prepLabel: String,
        val prepTime: String,
        val prepTimeAsMinutes: Int,
        val cookingLabel: String,
        val cookingTime: String,
        val cookTimeAsMinutes: Int
    )

    data class Ingredient(
        val ingredient: String
    )
}
