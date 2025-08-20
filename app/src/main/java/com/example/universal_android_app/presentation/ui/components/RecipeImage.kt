package com.example.universal_android_app.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RecipeImage(
    imageUrl: String,
    contentDescription: String?,
    imageType: ImageType,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    
    val imageHeight = responsiveImageHeight(imageType)
    
    Box(
        modifier = modifier.height(imageHeight),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(RectangleShape),
            contentScale = contentScale,
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { 
                isLoading = false
                isError = true
            }
        )
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (isError) {
            Text(
                text = "⚠",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

enum class ImageType { List, Detail }

@Composable
private fun responsiveImageHeight(type: ImageType): Dp {
    val configuration = LocalConfiguration.current
    val smallestWidthDp = configuration.smallestScreenWidthDp
    val isTablet = smallestWidthDp >= 600
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val detailHeight = when {
        isTablet && isLandscape -> 600.dp  // tablet + landscape
        isTablet && !isLandscape -> 400.dp // tablet + portrait
        !isTablet && isLandscape -> 300.dp // phone + landscape
        else -> 200.dp                     // phone + portrait
    }

    return when (type) {
        ImageType.Detail -> detailHeight
        ImageType.List -> if (isTablet) 400.dp else 200.dp
    }
}
