package com.example.universal_android_app.algorithm

import com.example.universal_android_app.domain.Recipe
import javax.inject.Inject

interface RecipeAlgorithm {
    fun sortByTotalTime(recipes: List<Recipe>, ascending: Boolean?): List<Recipe>
    fun sortByServingSize(recipes: List<Recipe>, ascending: Boolean?): List<Recipe>
    fun filterByServingRange(recipes: List<Recipe>, minServings: Int, maxServings: Int): List<Recipe>
}

/**
 * Algorithm for filtering and sorting recipes based on various criteria.
 * This is designed to be unit testable and provides different sorting and filtering options.
 */
class RecipeAlgorithmImpl @Inject constructor() : RecipeAlgorithm {
    
    /**
     * Sorts recipes by total cooking time (prep + cooking time)
     */
    override fun sortByTotalTime(recipes: List<Recipe>, ascending: Boolean?): List<Recipe> {
        return if (ascending == true) {
            recipes.sortedBy { it.recipeDetails.prepTimeAsMinutes + it.recipeDetails.cookTimeAsMinutes }
        } else {
            recipes.sortedByDescending { it.recipeDetails.prepTimeAsMinutes + it.recipeDetails.cookTimeAsMinutes }
        }
    }
    
    /**
     * Sorts recipes by serving size
     */
    override fun sortByServingSize(recipes: List<Recipe>, ascending: Boolean?): List<Recipe> {
        return if (ascending == true) {
            recipes.sortedBy { it.recipeDetails.amountNumber }
        } else {
            recipes.sortedByDescending { it.recipeDetails.amountNumber }
        }
    }
    
    /**
     * Filters recipes by serving size range
     */
    override fun filterByServingRange(recipes: List<Recipe>, minServings: Int, maxServings: Int): List<Recipe> {
        return recipes.filter { 
            it.recipeDetails.amountNumber in minServings..maxServings 
        }
    }
}
