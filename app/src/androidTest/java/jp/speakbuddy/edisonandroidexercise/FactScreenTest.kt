package jp.speakbuddy.edisonandroidexercise

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.speakbuddy.edisonandroidexercise.commons.TheResult
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FactScreenTest {

    // Change @get:Rule to @JvmField
    @JvmField
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: FactViewModel
    private lateinit var navController: TestNavHostController

    // Change @Before to @BeforeEach
    @BeforeEach
    fun setup() {
        viewModel = mockk(relaxed = true)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        every { viewModel.uiState } returns MutableStateFlow(TheResult.Success(FactUiState(
            fact = "Test fact",
            translationText = "Test translation",
            sourceLanguage = "English",
            targetLanguage = "Spanish",
            showLength = true,
            showMultipleCats = true
        )))
        every { viewModel.selectedLanguage } returns MutableStateFlow("Spanish")
        every { viewModel.getFlagCodes() } returns mapOf("Spanish" to "ES", "English" to "GB")
    }

    @Test
    fun factScreenDisplaysCorrectContent() {
        composeTestRule.setContent {
            FactScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Fact of the Day").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test fact").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test translation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Translation (Spanish)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Take Quiz").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next Story").assertIsDisplayed()
    }

    @Test
    fun clickingNextStoryButtonCallsUpdateFact() {
        composeTestRule.setContent {
            FactScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Next Story").performClick()
        verify { viewModel.updateFact() }
    }

    @Test
    fun clickingTakeQuizButtonShowsQuizSection() {
        composeTestRule.setContent {
            FactScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Take Quiz").performClick()
        composeTestRule.onNodeWithText("Quick Quiz").assertIsDisplayed()
    }

    @Test
    fun languageSelectorDisplaysCorrectly() {
        composeTestRule.setContent {
            FactScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Spanish").assertIsDisplayed()
    }

    @Test
    fun savedFactsNavigationIconIsDisplayed() {
        composeTestRule.setContent {
            FactScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Saved Facts").assertIsDisplayed()
    }
}