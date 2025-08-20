package com.example.universal_android_app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.universal_android_app.domain.Recipe
import com.example.universal_android_app.presentation.ui.components.RecipeImage
import com.example.universal_android_app.presentation.ui.components.AppScaffold
import com.example.universal_android_app.presentation.ui.components.ImageType
import com.example.universal_android_app.presentation.ui.components.RecipeDetailsCard
import com.example.universal_android_app.presentation.RecipeViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onBackClick: () -> Unit,
    viewModel: RecipeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recipe = uiState.filteredRecipes.getOrNull(recipeId)

    AppScaffold(
        title = "Recipe Details",
        onBackClick = onBackClick
    ) {
        if (recipe != null) {
            RecipeDetailContent(recipe = recipe)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found")
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Recipe title
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        item {
            // Recipe description
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        item {
            // Recipe image
            RecipeImage(
                imageUrl = recipe.imageUrl,
                contentDescription = recipe.imageDescription,
                imageType = ImageType.Detail,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            // Recipe details
            RecipeDetailsCard(recipe = recipe)
        }
        
        item {
            // Ingredients section
            IngredientsSection(ingredients = recipe.ingredients)
        }
    }
}

@Composable
private fun IngredientsSection(
    ingredients: List<Recipe.Ingredient>
) {
    Column {
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ingredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = ">",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = ingredient.ingredient,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
