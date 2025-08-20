package com.example.universal_android_app.data.mapper

import com.example.universal_android_app.data.RecipeListResponse
import com.example.universal_android_app.domain.Recipe

private fun RecipeListResponse.RecipeResponse.toRecipeData(baseUrl: String): Recipe = Recipe(
    title = title,
    description = description,
    imageUrl = thumbnail.buildImageUrl(baseUrl),
    imageDescription = thumbnailAlt,
    recipeDetails = Recipe.RecipeDetails(
        amountLabel = recipeDetails.amountLabel,
        amountNumber = recipeDetails.amountNumber,
        prepLabel = recipeDetails.prepLabel,
        prepTime = recipeDetails.prepTime,
        prepTimeAsMinutes = recipeDetails.prepTimeAsMinutes,
        cookingLabel = recipeDetails.cookingLabel,
        cookingTime = recipeDetails.cookingTime,
        cookTimeAsMinutes = recipeDetails.cookTimeAsMinutes
    ),
    ingredients = ingredients.map { Recipe.Ingredient(ingredient = it.ingredient) }
)

private fun String.buildImageUrl(baseUrl: String): String =
    if (startsWith("http")) {
        this
    } else {
        "$baseUrl$this"
    }

fun RecipeListResponse.toRecipeList(baseUrl: String): List<Recipe> = this.recipes.map { it.toRecipeData(baseUrl) }
