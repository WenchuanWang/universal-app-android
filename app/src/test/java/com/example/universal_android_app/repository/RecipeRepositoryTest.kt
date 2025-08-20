package com.example.universal_android_app.repository

import android.content.Context
import com.example.universal_android_app.MainDispatcherRule
import com.example.universal_android_app.data.RecipeRepositoryImpl
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.data.remote.RecipeApiService
import com.example.universal_android_app.data.RecipeListResponse
import com.example.universal_android_app.domain.RecipeRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: RecipeRepository
    private lateinit var mockApiService: RecipeApiService
    private lateinit var mockContext: Context
    private lateinit var mockAssetManager: android.content.res.AssetManager
    private val baseUrl = "https://test.com/"

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockContext = mockk()
        mockAssetManager = mockk()
        
        every { mockContext.assets } returns mockAssetManager
    }

    private fun createTestRecipe(
        title: String = "Test Recipe",
        description: String = "Test Description",
        imageUrl: String = "test-image.jpg",
        imageDescription: String = "Test Image",
        servingSize: Int = 4,
        prepTime: String = "10 mins",
        prepTimeMinutes: Int = 10,
        cookingTime: String = "20 mins",
        cookingTimeMinutes: Int = 20,
        ingredients: List<String> = listOf("Ingredient 1", "Ingredient 2")
    ): Recipe {
        return Recipe(
            title = title,
            description = description,
            imageUrl = imageUrl,
            imageDescription = imageDescription,
            recipeDetails = Recipe.RecipeDetails(
                amountLabel = "Serves",
                amountNumber = servingSize,
                prepLabel = "Prep",
                prepTime = prepTime,
                prepTimeAsMinutes = prepTimeMinutes,
                cookingLabel = "Cooking",
                cookingTime = cookingTime,
                cookTimeAsMinutes = cookingTimeMinutes
            ),
            ingredients = ingredients.map { Recipe.Ingredient(it) }
        )
    }

    private fun createTestRecipeResponse(
        title: String = "Test Recipe",
        description: String = "Test Description",
        thumbnail: String = "test-image.jpg",
        thumbnailAlt: String = "Test Image",
        servingSize: Int = 4,
        prepTime: String = "10 mins",
        prepTimeMinutes: Int = 10,
        cookingTime: String = "20 mins",
        cookingTimeMinutes: Int = 20,
        ingredients: List<String> = listOf("Ingredient 1", "Ingredient 2")
    ): RecipeListResponse.RecipeResponse {
        return RecipeListResponse.RecipeResponse(
            title = title,
            description = description,
            thumbnail = thumbnail,
            thumbnailAlt = thumbnailAlt,
            recipeDetails = RecipeListResponse.RecipeDetailsResponse(
                amountLabel = "Serves",
                amountNumber = servingSize,
                prepLabel = "Prep",
                prepTime = prepTime,
                prepNote = null,
                cookingLabel = "Cooking",
                cookingTime = cookingTime,
                cookTimeAsMinutes = cookingTimeMinutes,
                prepTimeAsMinutes = prepTimeMinutes
            ),
            ingredients = ingredients.map { RecipeListResponse.IngredientResponse(it) }
        )
    }

    @Test
    fun `Given local recipes are available When getRecipes is called Then success with local recipes should be returned`() = runTest {
        // Given
        val testRecipes = listOf(
            createTestRecipeResponse("Recipe 1"),
            createTestRecipeResponse("Recipe 2")
        )
        val jsonResponse = """
            {
                "recipes": [
                    {
                        "dynamicTitle": "Recipe 1",
                        "dynamicDescription": "Test Description",
                        "dynamicThumbnail": "recipe1.jpg",
                        "dynamicThumbnailAlt": "Recipe 1 Image",
                        "recipeDetails": {
                            "amountLabel": "Serves",
                            "amountNumber": 4,
                            "prepLabel": "Prep",
                            "prepTime": "10 mins",
                            "prepNote": null,
                            "cookingLabel": "Cooking",
                            "cookingTime": "20 mins",
                            "cookTimeAsMinutes": 20,
                            "prepTimeAsMinutes": 10
                        },
                        "ingredients": [
                            {"ingredient": "Ingredient 1"},
                            {"ingredient": "Ingredient 2"}
                        ]
                    }
                ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonResponse.toByteArray())
        every { mockAssetManager.open("recipes.json") } returns inputStream

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isSuccess)
        val recipes = result.getOrNull()!!
        assertEquals(1, recipes.size)
        assertEquals("Recipe 1", recipes[0].title)
        assertEquals("Test Description", recipes[0].description)
        assertEquals("${baseUrl}recipe1.jpg", recipes[0].imageUrl)
        assertEquals("Recipe 1 Image", recipes[0].imageDescription)
        assertEquals(4, recipes[0].recipeDetails.amountNumber)
        assertEquals(2, recipes[0].ingredients.size)
    }

    @Test
    fun `Given local recipes are empty When getRecipes is called Then fallback to API should occur`() = runTest {
        // Given
        val testRecipes = listOf(createTestRecipeResponse("API Recipe"))
        val apiResponse = RecipeListResponse(testRecipes)
        
        every { mockAssetManager.open("recipes.json") } throws IOException("File not found")
        coEvery { mockApiService.getRecipes() } returns apiResponse

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isSuccess)
        val recipes = result.getOrNull()!!
        assertEquals(1, recipes.size)
        assertEquals("API Recipe", recipes[0].title)
        coVerify { mockApiService.getRecipes() }
    }

    @Test
    fun `Given both local and API fail When getRecipes is called Then failure should be returned`() = runTest {
        // Given
        every { mockAssetManager.open("recipes.json") } throws IOException("File not found")
        coEvery { mockApiService.getRecipes() } throws RuntimeException("API Error")

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `Given recipes with different titles When searchRecipes is called Then recipes should be filtered by title`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Chicken Recipe", "Description"),
            createTestRecipe("Beef Recipe", "Description"),
            createTestRecipe("Fish Recipe", "Description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("chicken", recipes)

        // Then
        assertEquals(1, result.size)
        assertEquals("Chicken Recipe", result[0].title)
    }

    @Test
    fun `Given recipes with different descriptions When searchRecipes is called Then recipes should be filtered by description`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Recipe 1", "Chicken description"),
            createTestRecipe("Recipe 2", "Beef description"),
            createTestRecipe("Recipe 3", "Fish description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("chicken", recipes)

        // Then
        assertEquals(1, result.size)
        assertEquals("Recipe 1", result[0].title)
    }

    @Test
    fun `Given recipes with different ingredients When searchRecipes is called Then recipes should be filtered by ingredients`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Recipe 1", "Description", ingredients = listOf("Chicken", "Salt")),
            createTestRecipe("Recipe 2", "Description", ingredients = listOf("Beef", "Pepper")),
            createTestRecipe("Recipe 3", "Description", ingredients = listOf("Fish", "Lemon"))
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("chicken", recipes)

        // Then
        assertEquals(1, result.size)
        assertEquals("Recipe 1", result[0].title)
    }

    @Test
    fun `Given search query with different case When searchRecipes is called Then search should be case insensitive`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Chicken Recipe", "Description"),
            createTestRecipe("Beef Recipe", "Description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("CHICKEN", recipes)

        // Then
        assertEquals(1, result.size)
        assertEquals("Chicken Recipe", result[0].title)
    }

    @Test
    fun `Given search query with no matches When searchRecipes is called Then empty list should be returned`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Chicken Recipe", "Description"),
            createTestRecipe("Beef Recipe", "Description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("pork", recipes)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `Given recipes with partial matches When searchRecipes is called Then partial matches should be returned`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Chicken Curry", "Description"),
            createTestRecipe("Chicken Soup", "Description"),
            createTestRecipe("Beef Stew", "Description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("chicken", recipes)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.title.contains("Chicken", ignoreCase = true) })
    }

    @Test
    fun `Given search query with special characters When searchRecipes is called Then special characters should be handled correctly`() {
        // Given
        val recipes = listOf(
            createTestRecipe("Chicken & Rice", "Description"),
            createTestRecipe("Beef Stew", "Description")
        )
        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.searchRecipes("&", recipes)

        // Then
        assertEquals(1, result.size)
        assertEquals("Chicken & Rice", result[0].title)
    }

    @Test
    fun `Given empty JSON response When getRecipes is called Then empty recipe list should be returned`() = runTest {
        // Given
        val emptyJson = """{"recipes": []}"""
        val inputStream = ByteArrayInputStream(emptyJson.toByteArray())
        every { mockAssetManager.open("recipes.json") } returns inputStream
        val testRecipes = emptyList<RecipeListResponse.RecipeResponse>()
        val apiResponse = RecipeListResponse(testRecipes)
        coEvery { mockApiService.getRecipes() } returns apiResponse

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isSuccess)
        val recipes = result.getOrNull()!!
        assertTrue(recipes.isEmpty())
    }

    @Test
    fun `Given recipe with relative image URL When getRecipes is called Then image URL should be correctly formatted`() = runTest {
        // Given
        val jsonResponse = """
            {
                "recipes": [
                    {
                        "dynamicTitle": "Test Recipe",
                        "dynamicDescription": "Test Description",
                        "dynamicThumbnail": "/images/recipe.jpg",
                        "dynamicThumbnailAlt": "Test Image",
                        "recipeDetails": {
                            "amountLabel": "Serves",
                            "amountNumber": 4,
                            "prepLabel": "Prep",
                            "prepTime": "10 mins",
                            "prepNote": null,
                            "cookingLabel": "Cooking",
                            "cookingTime": "20 mins",
                            "cookTimeAsMinutes": 20,
                            "prepTimeAsMinutes": 10
                        },
                        "ingredients": [
                            {"ingredient": "Ingredient 1"}
                        ]
                    }
                ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonResponse.toByteArray())
        every { mockAssetManager.open("recipes.json") } returns inputStream

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isSuccess)
        val recipes = result.getOrNull()!!
        assertEquals("$baseUrl/images/recipe.jpg", recipes[0].imageUrl)
    }

    @Test
    fun `Given recipe with absolute image URL When getRecipes is called Then absolute URL should be preserved`() = runTest {
        // Given
        val jsonResponse = """
            {
                "recipes": [
                    {
                        "dynamicTitle": "Test Recipe",
                        "dynamicDescription": "Test Description",
                        "dynamicThumbnail": "https://external.com/image.jpg",
                        "dynamicThumbnailAlt": "Test Image",
                        "recipeDetails": {
                            "amountLabel": "Serves",
                            "amountNumber": 4,
                            "prepLabel": "Prep",
                            "prepTime": "10 mins",
                            "prepNote": null,
                            "cookingLabel": "Cooking",
                            "cookingTime": "20 mins",
                            "cookTimeAsMinutes": 20,
                            "prepTimeAsMinutes": 10
                        },
                        "ingredients": [
                            {"ingredient": "Ingredient 1"}
                        ]
                    }
                ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonResponse.toByteArray())
        every { mockAssetManager.open("recipes.json") } returns inputStream

        repository = RecipeRepositoryImpl(mockApiService, mockContext, baseUrl)

        // When
        val result = repository.getRecipes()

        // Then
        assertTrue(result.isSuccess)
        val recipes = result.getOrNull()!!
        assertEquals("https://external.com/image.jpg", recipes[0].imageUrl)
    }
}
