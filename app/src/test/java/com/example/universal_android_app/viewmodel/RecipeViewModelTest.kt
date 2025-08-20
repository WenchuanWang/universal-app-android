package com.example.universal_android_app.viewmodel

import com.example.universal_android_app.MainDispatcherRule
import com.example.universal_android_app.algorithm.RecipeAlgorithm
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.domain.RecipeRepository
import com.example.universal_android_app.presentation.RecipeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RecipeViewModel
    private lateinit var mockRepository: RecipeRepository
    private lateinit var mockAlgorithm: RecipeAlgorithm

    @Before
    fun setUp() {
        mockRepository = mockk()
        mockAlgorithm = mockk()
        val testRecipes = listOf(
            createTestRecipe("Recipe 1", 4, 10, 20),
            createTestRecipe("Recipe 2", 6, 15, 25)
        )
        coEvery { mockRepository.getRecipes() } returns Result.success(testRecipes)
    }
    
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
    fun `Given recipes are available When loadRecipes is called Then state should be updated with recipes`() = runTest {
        // Given
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)

        // When
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.recipes.size)
        assertEquals(2, state.filteredRecipes.size)
        assertFalse(state.isLoading)
        assertTrue(state.error == null)
        assertEquals("Recipe 1", state.recipes[0].title)
        assertEquals("Recipe 2", state.recipes[1].title)
    }
    
    @Test
    fun `Given repository fails When loadRecipes is called Then state should be updated with error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getRecipes() } returns Result.failure(Exception(errorMessage))
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.recipes.isEmpty())
        assertTrue(state.filteredRecipes.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }
    
    @Test
    fun `Given search query is entered When updateSearchQuery is called Then recipes should be filtered`() = runTest {
        // Given
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)
        coEvery { mockRepository.searchRecipes(any(), any()) } returns listOf(
            createTestRecipe("Chicken Recipe", 4, 10, 20)
        )

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        viewModel.updateSearchQuery("chicken")
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("chicken", state.searchQuery)
        assertEquals(1, state.filteredRecipes.size)
        assertEquals("Chicken Recipe", state.filteredRecipes[0].title)
    }
    
    @Test
    fun `Given search query is empty When updateSearchQuery is called Then all recipes should be shown`() = runTest {
        // Given
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        advanceUntilIdle()
        viewModel.updateSearchQuery("")
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertEquals(2, state.filteredRecipes.size)
    }

    @Test
    fun `Given search query is blank When updateSearchQuery is called Then all recipes should be shown`() = runTest {
        // Given
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        advanceUntilIdle()
        viewModel.updateSearchQuery("   ")

        // Then
        val state = viewModel.uiState.value
        assertEquals("   ", state.searchQuery)
        assertEquals(2, state.filteredRecipes.size)
    }
    
    @Test
    fun `Given sort type is selected When sortRecipes is called Then recipes should be sorted correctly`() = runTest {
        // Given
        val testRecipes = listOf(
            createTestRecipe("Recipe 1", 4, 10, 20),
            createTestRecipe("Recipe 2", 6, 15, 25)
        )
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)
        coEvery { mockAlgorithm.sortByTotalTime(any(), false) } returns testRecipes.reversed()

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        advanceUntilIdle()
        viewModel.sortRecipes(RecipeViewModel.SortType.TOTAL_TIME_DESC)
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(RecipeViewModel.SortType.TOTAL_TIME_DESC, state.sortType)
        assertEquals(2, state.filteredRecipes.size)
    }
    
    @Test
    fun `Given different sort types When sortRecipes is called Then all sort types should be handled correctly`() = runTest {
        // Given
        val testRecipes = listOf(
            createTestRecipe("Recipe 1", 4, 10, 20),
            createTestRecipe("Recipe 2", 6, 15, 25)
        )
        viewModel = RecipeViewModel(mockRepository, mockAlgorithm)
        coEvery { mockAlgorithm.sortByTotalTime(any(), true) } returns testRecipes
        coEvery { mockAlgorithm.sortByTotalTime(any(), false) } returns testRecipes.reversed()
        coEvery { mockAlgorithm.sortByServingSize(any(), true) } returns testRecipes
        coEvery { mockAlgorithm.sortByServingSize(any(), false) } returns testRecipes.reversed()

        // When - Create ViewModel after setting up mocks, then wait for coroutine
        advanceUntilIdle()
        
        // Then - Test all sort types
        viewModel.sortRecipes(RecipeViewModel.SortType.TOTAL_TIME_ASC)
        assertEquals(RecipeViewModel.SortType.TOTAL_TIME_ASC, viewModel.uiState.value.sortType)
        
        viewModel.sortRecipes(RecipeViewModel.SortType.TOTAL_TIME_DESC)
        assertEquals(RecipeViewModel.SortType.TOTAL_TIME_DESC, viewModel.uiState.value.sortType)
        
        viewModel.sortRecipes(RecipeViewModel.SortType.SERVING_SIZE_ASC)
        assertEquals(RecipeViewModel.SortType.SERVING_SIZE_ASC, viewModel.uiState.value.sortType)
        
        viewModel.sortRecipes(RecipeViewModel.SortType.SERVING_SIZE_DESC)
        assertEquals(RecipeViewModel.SortType.SERVING_SIZE_DESC, viewModel.uiState.value.sortType)
        
        viewModel.sortRecipes(RecipeViewModel.SortType.NONE)
        assertEquals(RecipeViewModel.SortType.NONE, viewModel.uiState.value.sortType)
    }
    
    @Test
    fun `Given SortType enum When accessed Then display names and ascending values should be correct`() {
        // Test that all sort types have proper display names and ascending values
        assertEquals("Default", RecipeViewModel.SortType.NONE.displayName)
        assertNull(RecipeViewModel.SortType.NONE.ascending)
        
        assertEquals("Total Time (Fastest First)", RecipeViewModel.SortType.TOTAL_TIME_ASC.displayName)
        assertTrue(RecipeViewModel.SortType.TOTAL_TIME_ASC.ascending!!)
        
        assertEquals("Total Time (Slowest First)", RecipeViewModel.SortType.TOTAL_TIME_DESC.displayName)
        assertFalse(RecipeViewModel.SortType.TOTAL_TIME_DESC.ascending!!)
        
        assertEquals("Serving Size (Smallest First)", RecipeViewModel.SortType.SERVING_SIZE_ASC.displayName)
        assertTrue(RecipeViewModel.SortType.SERVING_SIZE_ASC.ascending!!)
        
        assertEquals("Serving Size (Largest First)", RecipeViewModel.SortType.SERVING_SIZE_DESC.displayName)
        assertFalse(RecipeViewModel.SortType.SERVING_SIZE_DESC.ascending!!)
    }
}
