package com.example.universal_android_app.algorithm

import com.example.universal_android_app.domain.Recipe
import org.junit.Assert.*
import org.junit.Test

class RecipeAlgorithmImplTest {
    
    private val algorithm = RecipeAlgorithmImpl()

    private fun createTestRecipe(
        title: String,
        servingSize: Int,
        prepTimeMinutes: Int,
        cookTimeMinutes: Int
    ): Recipe {
        return Recipe(
            title = title,
            description = "Test description",
            imageUrl = "/test/image.jpg",
            imageDescription = "Test image",
            recipeDetails = Recipe.RecipeDetails(
                amountLabel = "Serves",
                amountNumber = servingSize,
                prepLabel = "Prep",
                prepTime = "${prepTimeMinutes}m",
                cookingLabel = "Cooking",
                cookingTime = "${cookTimeMinutes}m",
                cookTimeAsMinutes = cookTimeMinutes,
                prepTimeAsMinutes = prepTimeMinutes
            ),
            ingredients = listOf(
                Recipe.Ingredient("Test ingredient 1"),
                Recipe.Ingredient("Test ingredient 2")
            )
        )
    }
    
    @Test
    fun `Given recipes with different total times When sortByTotalTime is called with ascending order Then recipes should be sorted by total time ascending`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Slow Recipe", 4, 30, 60), // Total: 90
            createTestRecipe("Quick Recipe", 2, 10, 15), // Total: 25
            createTestRecipe("Medium Recipe", 6, 20, 30) // Total: 50
        )
        
        // When
        val result = algorithm.sortByTotalTime(recipes, true)
        
        // Then
        assertEquals(3, result.size)
        assertEquals("Quick Recipe", result[0].title)
        assertEquals("Medium Recipe", result[1].title)
        assertEquals("Slow Recipe", result[2].title)
    }
    
    @Test
    fun `Given recipes with different total times When sortByTotalTime is called with descending order Then recipes should be sorted by total time descending`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Quick Recipe", 2, 10, 15), // Total: 25
            createTestRecipe("Slow Recipe", 4, 30, 60), // Total: 90
            createTestRecipe("Medium Recipe", 6, 20, 30) // Total: 50
        )
        
        // When
        val result = algorithm.sortByTotalTime(recipes, false)
        
        // Then
        assertEquals(3, result.size)
        assertEquals("Slow Recipe", result[0].title)
        assertEquals("Medium Recipe", result[1].title)
        assertEquals("Quick Recipe", result[2].title)
    }
    
    @Test
    fun `Given recipes with different serving sizes When sortByServingSize is called with ascending order Then recipes should be sorted by serving size ascending`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Small Recipe", 2, 15, 20),
            createTestRecipe("Large Recipe", 8, 20, 30),
            createTestRecipe("Medium Recipe", 4, 10, 25)
        )
        
        // When
        val result = algorithm.sortByServingSize(recipes, true)
        
        // Then
        assertEquals(3, result.size)
        assertEquals("Small Recipe", result[0].title)
        assertEquals("Medium Recipe", result[1].title)
        assertEquals("Large Recipe", result[2].title)
    }
    
    @Test
    fun `Given recipes with different serving sizes When sortByServingSize is called with descending order Then recipes should be sorted by serving size descending`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Medium Recipe", 4, 10, 25),
            createTestRecipe("Small Recipe", 2, 15, 20),
            createTestRecipe("Large Recipe", 8, 20, 30)
        )
        
        // When
        val result = algorithm.sortByServingSize(recipes, false)
        
        // Then
        assertEquals(3, result.size)
        assertEquals("Large Recipe", result[0].title)
        assertEquals("Medium Recipe", result[1].title)
        assertEquals("Small Recipe", result[2].title)
    }
}
