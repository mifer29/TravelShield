package es.uc3m.android.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import es.uc3m.android.travelshield.screens.WriteReviewScreen
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class WriteReviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun writeReviewTest() {
        // Set the content to directly test WriteReviewScreen
        composeTestRule.setContent {
            val navController = rememberNavController()
            WriteReviewScreen(
                countryName = "Australia",
                navController = navController
            )
        }

        // Wait for the UI to be idle before performing actions
        composeTestRule.waitForIdle()

        // Test values
        val reviewText = "Amazing trip!"
        val rating = 4f

        // Fill in the review text field
        composeTestRule.onNodeWithText("Your review")
            .performTextInput(reviewText)

        // Ensure the Rating text exists and is displayed
        composeTestRule.onNodeWithText("Rating:")
            .assertIsDisplayed()

        // Adjust the slider for the rating (we assume the slider has a tag for easier selection)
        composeTestRule.onNodeWithTag("ratingSlider")
            .performClick() // Adjust this interaction depending on how the slider is structured (e.g., set value to 4)

        // Check if the correct rating is displayed (make sure to match dynamic values)
        composeTestRule.onNodeWithText("Rating: ${rating.toInt()}")
            .assertIsDisplayed()

        // Check if the Submit Review button is visible and enabled
        composeTestRule.onNodeWithText("Submit Review")
            .assertIsDisplayed()
            .performClick()


    }
}
