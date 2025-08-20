package com.example.universal_android_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.universal_android_app.algorithm.RecipeAlgorithm
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.domain.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val algorithm: RecipeAlgorithm
) : ViewModel() {

    data class RecipeUiState(
        val recipes: List<Recipe> = emptyList(),
        val filteredRecipes: List<Recipe> = emptyList(),
        val selectedRecipe: Recipe? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val sortType: SortType = SortType.NONE
    )

    enum class SortType(val displayName: String, val ascending: Boolean?) {
        NONE("Default", null),
        TOTAL_TIME_ASC("Total Time (Fastest First)", true),
        TOTAL_TIME_DESC("Total Time (Slowest First)", false),
        SERVING_SIZE_ASC("Serving Size (Smallest First)", true),
        SERVING_SIZE_DESC("Serving Size (Largest First)", false)
    }
    
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()
    
    init {
        loadRecipes()
    }
    
    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getRecipes()
                .onSuccess { recipes ->
                    _uiState.value = _uiState.value.copy(
                        recipes = recipes,
                        filteredRecipes = recipes,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        performSearch(query)
        // Sort final list
        _uiState.value.sortType
            .takeIf { it != SortType.NONE }
            ?.let { sortRecipes(it) }
    }
    
    private fun performSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(filteredRecipes = _uiState.value.recipes)
            } else {
                val searchResults = repository.searchRecipes(query, _uiState.value.recipes)
                _uiState.value = _uiState.value.copy(filteredRecipes = searchResults)
            }
        }
    }
    
    fun sortRecipes(sortType: SortType) {
        val currentRecipes = _uiState.value.filteredRecipes
        val defaultRecipes = _uiState.value.recipes
        
        val sortedRecipes = when (sortType) {
            SortType.TOTAL_TIME_ASC, SortType.TOTAL_TIME_DESC ->
                algorithm.sortByTotalTime(currentRecipes, sortType.ascending)
            SortType.SERVING_SIZE_ASC, SortType.SERVING_SIZE_DESC ->
                algorithm.sortByServingSize(currentRecipes, sortType.ascending)
            SortType.NONE -> defaultRecipes
        }
        
        _uiState.value = _uiState.value.copy(
            filteredRecipes = sortedRecipes,
            sortType = sortType
        )
    }
}
