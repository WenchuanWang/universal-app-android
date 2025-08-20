package com.example.universal_android_app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.universal_android_app.domain.Recipe

@Composable
fun RecipeDetailsCard(
    recipe: Recipe
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RecipeDetailItem(
                label = recipe.recipeDetails.amountLabel,
                value = recipe.recipeDetails.amountNumber.toString()
            )
            VerticalDivider()
            RecipeDetailItem(
                label = recipe.recipeDetails.prepLabel,
                value = recipe.recipeDetails.prepTime
            )
            VerticalDivider()
            RecipeDetailItem(
                label = recipe.recipeDetails.cookingLabel,
                value = recipe.recipeDetails.cookingTime
            )
        }
        HorizontalDivider()
    }
}

@Composable
private fun RecipeDetailItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
