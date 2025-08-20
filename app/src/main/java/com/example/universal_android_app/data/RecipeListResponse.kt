package com.example.universal_android_app.data

import com.google.gson.annotations.SerializedName

data class RecipeListResponse(
    @SerializedName("recipes")
    val recipes: List<RecipeResponse>
) {
    data class RecipeResponse(
        @SerializedName("dynamicTitle")
        val title: String,
        @SerializedName("dynamicDescription")
        val description: String,
        @SerializedName("dynamicThumbnail")
        val thumbnail: String,
        @SerializedName("dynamicThumbnailAlt")
        val thumbnailAlt: String,
        @SerializedName("recipeDetails")
        val recipeDetails: RecipeDetailsResponse,
        @SerializedName("ingredients")
        val ingredients: List<IngredientResponse>
    )

    data class RecipeDetailsResponse(
        @SerializedName("amountLabel")
        val amountLabel: String,
        @SerializedName("amountNumber")
        val amountNumber: Int,
        @SerializedName("prepLabel")
        val prepLabel: String,
        @SerializedName("prepTime")
        val prepTime: String,
        @SerializedName("prepNote")
        val prepNote: String?,
        @SerializedName("cookingLabel")
        val cookingLabel: String,
        @SerializedName("cookingTime")
        val cookingTime: String,
        @SerializedName("cookTimeAsMinutes")
        val cookTimeAsMinutes: Int,
        @SerializedName("prepTimeAsMinutes")
        val prepTimeAsMinutes: Int
    )

    data class IngredientResponse(
        @SerializedName("ingredient")
        val ingredient: String
    )
}
