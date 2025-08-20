package com.example.universal_android_app

import android.content.pm.ActivityInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeUITest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testRecipeListScreenDisplay() {
        // Test that the app loads properly and shows content
        // The loading state is very brief when loading from local assets, so we test the end result
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("RECIPE").fetchSemanticsNodes().size > 0
        }

        // Verify that the app has loaded successfully by checking for key UI elements
        composeTestRule.onNodeWithText("Recipe Collection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search recipes...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Default").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("RECIPE")[0].assertIsDisplayed()
    }
    
    @Test
    fun testSearchFunctionality() {
        // Test search input
        composeTestRule.onNodeWithText("Search recipes...").performTextInput("pork")
        
        // Verify search query is entered
        composeTestRule.onNodeWithText("pork").assertIsDisplayed()
    }
    
    @Test
    fun testSortDropdownIsClickable() {
        // Find Sort button
        val node = composeTestRule.onNode(
            hasText("Default") and hasClickAction()
                    and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) // optional disambiguation
        )
        node.assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed")
        )

        // Click on sort dropdown
        node.performClick()

        // Verify Sort button state
        composeTestRule.onNode(
            hasText("Default") and hasClickAction()
                    and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) // optional disambiguation
        ).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded")
        )
        // Verify Dropdown Menu appears
        composeTestRule.onNodeWithTag("sortMenu").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("sortItem_Total Time (Fastest First)").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sortItem_Total Time (Slowest First)").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sortItem_Serving Size (Smallest First)").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sortItem_Serving Size (Largest First)").assertIsDisplayed()
    }

    @Test
    fun testSortDropdownSelection() {
        // Click on sort dropdown
        composeTestRule.onNode(
            hasText("Default") and hasClickAction()
                    and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) // optional disambiguation
        ).performClick()

        // Click a new menu item
        composeTestRule.onNodeWithText("Total Time (Fastest First)", useUnmergedTree = true)
            .performClick()

        // Verify Dropdown Menu disappears and sort button state
        composeTestRule.onNodeWithTag("sortMenu").assertDoesNotExist()
        composeTestRule.onNode(
            hasText("Total Time (Fastest First)") and hasClickAction()
                    and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) // optional disambiguation
        ).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed")
        ).assertIsDisplayed()
    }
    
    @Test
    fun testRecipeCountDisplay() {
        // Wait for recipes to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("RECIPE").fetchSemanticsNodes().size > 0
        }
        
        // Verify recipe count is displayed
        composeTestRule.onNode(hasText("recipes found", substring = true)).assertIsDisplayed()
    }
    
    @Test
    fun testRecipeCardDisplay() {
        // Wait for recipes to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("RECIPE").fetchSemanticsNodes().size > 0
        }
        
        // Test that recipe cards have proper content descriptions
        composeTestRule.onAllNodesWithText("RECIPE")[0].assertIsDisplayed()
    }
    
    @Test
    fun testRecipeCardClick() {
        // Wait for recipes to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("RECIPE").fetchSemanticsNodes().size > 0
        }
        
        // Click on first recipe card
        composeTestRule.onAllNodesWithText("RECIPE")[0].performClick()
        
        // Verify navigation to detail screen
        composeTestRule.onNodeWithText("Recipe Details").assertIsDisplayed()
    }
    
    @Test
    fun testBackNavigation() {
        // Navigate to detail screen first
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("RECIPE").fetchSemanticsNodes().size > 0
        }
        
        composeTestRule.onAllNodesWithText("RECIPE")[0].performClick()
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Verify back to list screen
        composeTestRule.onNodeWithText("Recipe Collection").assertIsDisplayed()
    }
    
    @Test
    fun testOrientationChangeToLandscape() {
        // Rotate to landscape
        composeTestRule.activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Let the configuration change propagate
        composeTestRule.waitForIdle()

        // Now assert UI that depends on isLandscape
        composeTestRule.onNodeWithContentDescription("Landscape Grid Layout").assertIsDisplayed()
    }

    @Test
    fun testOrientationChangeToPortrait() {
        // Rotate to landscape
        composeTestRule.activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Let the configuration change propagate
        composeTestRule.waitForIdle()

        // Now assert UI that depends on isLandscape
        composeTestRule.onNodeWithContentDescription("Portrait List Layout").assertIsDisplayed()
    }
    
    @Test
    fun testSearchClearButton() {
        // Enter search text
        composeTestRule.onNodeWithText("Search recipes...").performTextInput("test")
        
        // Verify clear button appears
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
        
        // Click clear button
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        
        // Verify search is cleared
        composeTestRule.onNodeWithText("test").assertDoesNotExist()
    }
}
