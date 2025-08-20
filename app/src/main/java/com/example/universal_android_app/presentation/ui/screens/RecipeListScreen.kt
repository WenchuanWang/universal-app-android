package com.example.universal_android_app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.presentation.RecipeViewModel
import com.example.universal_android_app.presentation.RecipeViewModel.SortType
import com.example.universal_android_app.presentation.ui.components.AppScaffold
import com.example.universal_android_app.presentation.ui.components.ErrorMessage
import com.example.universal_android_app.presentation.ui.components.ImageType
import com.example.universal_android_app.presentation.ui.components.LoadingSpinner
import com.example.universal_android_app.presentation.ui.components.RecipeDetailsCard
import com.example.universal_android_app.presentation.ui.components.RecipeImage

@Composable
fun RecipeListScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: RecipeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    AppScaffold(
        title = "Recipe Collection"
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search and sort controls
            RecipeControls(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                currentSortType = uiState.sortType,
                onSortTypeSelected = viewModel::sortRecipes,
                recipeCount = uiState.filteredRecipes.size
            )

            // Content based on loading state
            when {
                uiState.isLoading -> {
                    LoadingSpinner()
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error!!,
                        onRetry = { /* Reload recipes */ }
                    )
                }
                else -> {
                    RecipeContent(
                        recipes = uiState.filteredRecipes,
                        isLandscape = isLandscape,
                        onRecipeClick = onRecipeClick
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeControls(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    recipeCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        com.example.universal_android_app.presentation.ui.components.SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortDropdown(
                currentSortType = currentSortType,
                onSortTypeSelected = onSortTypeSelected,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "$recipeCount recipes found",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RecipeContent(
    recipes: List<Recipe>,
    isLandscape: Boolean,
    onRecipeClick: (Int) -> Unit
) {
    if (isLandscape) {
        // Grid layout for landscape
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.semantics { contentDescription = "Landscape Grid Layout" }
        ) {
            itemsIndexed(recipes) { index, recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(index) }
                )
            }
        }
    } else {
        // List layout for portrait
        LazyColumn(
            modifier = Modifier.semantics { contentDescription = "Portrait List Layout" }
        ) {
            itemsIndexed(recipes) { index, recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(index) }
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            RecipeImage(
                imageUrl = recipe.imageUrl,
                contentDescription = recipe.imageDescription,
                imageType = ImageType.List,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RECIPE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            RecipeDetailsCard(recipe = recipe)
        }
    }
}

@Composable
private fun SortDropdown(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    val sortOptions = SortType.entries
    val currentSortText = currentSortType.displayName
    
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .wrapContentWidth()
                .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" }
        ) {
            Text(
                text = currentSortText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .testTag("sortMenu")
                .semantics { contentDescription = "Sort menu" }
        ) {
            sortOptions.forEach { sortType ->
                DropdownMenuItem(
                    modifier = Modifier.testTag("sortItem_${sortType.displayName}"),
                    text = {
                        Text(
                            text = sortType.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onSortTypeSelected(sortType)
                        expanded = false
                    },
                    leadingIcon = {
                        if (sortType == currentSortType) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}
