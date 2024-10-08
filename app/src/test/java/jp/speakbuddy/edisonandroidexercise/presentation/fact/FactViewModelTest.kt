package jp.speakbuddy.edisonandroidexercise.presentation.fact

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import jp.speakbuddy.edisonandroidexercise.domain.model.Quiz
import jp.speakbuddy.edisonandroidexercise.domain.model.Translation
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetLatestFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetQuizUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.SaveFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.SearchSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.TranslateUseCase
import jp.speakbuddy.edisonandroidexercise.presentation.commons.TheResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@ExperimentalCoroutinesApi
class FactViewModelTest {

    private lateinit var viewModel: FactViewModel
    private lateinit var getFactUseCase: GetFactUseCase
    private lateinit var saveFactUseCase: SaveFactUseCase
    private lateinit var translateUseCase: TranslateUseCase
    private lateinit var getSavedFactsUseCase: GetSavedFactsUseCase
    private lateinit var getQuizUseCase: GetQuizUseCase
    private lateinit var getLatestFactUseCase: GetLatestFactUseCase
    private lateinit var searchSavedFactsUseCase: SearchSavedFactsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getFactUseCase = mockk()
        saveFactUseCase = mockk(relaxed = true)
        translateUseCase = mockk()
        getSavedFactsUseCase = mockk()
        getQuizUseCase = mockk()
        getLatestFactUseCase = mockk()
        searchSavedFactsUseCase = mockk()

        viewModel = FactViewModel(
            getFactUseCase,
            saveFactUseCase,
            translateUseCase,
            getSavedFactsUseCase,
            getQuizUseCase,
            getLatestFactUseCase,
            searchSavedFactsUseCase,
            testDispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateFact should update uiState with new fact and translation`() = runTest {
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val translation = Translation("Translated test fact")

        coEvery { getFactUseCase() } returns fact
        coEvery { translateUseCase(any(), any(), any()) } returns translation

        viewModel.updateFact()

        viewModel.uiState.test {
            // Collect and print all emitted states
            repeat(3) { // Adjust this number if you expect more or fewer states
                val state = awaitItem()
                println("Emitted state: $state")

                when (state) {
                    is TheResult.Loading -> {
                        println("Loading state received")
                    }

                    is TheResult.Success -> {
                        println("Success state received")
                        assertEquals(fact.text, state.data.fact)
                        assertEquals(translation.translatedText, state.data.translationText)
                    }

                    is TheResult.Error -> {
                        println("Error state received: ${state.message}")
                    }
                }
            }

            // Ensure no more items are emitted
            expectNoEvents()
        }
    }

    @Test
    fun `updateFact should emit error state when getFactUseCase fails`() = runTest {
        // Arrange
        val errorMessage = "Failed to fetch fact"
        coEvery { getFactUseCase() } throws Exception(errorMessage)

        // Act
        viewModel.updateFact()

        // Assert
        viewModel.uiState.test {
            var foundError = false
            repeat(3) { // Allow for up to 3 emissions
                when (val state = awaitItem()) {
                    is TheResult.Loading -> {
                        println("Loading state received")
                    }
                    is TheResult.Error -> {
                        assertTrue(state.message.contains(errorMessage), 
                            "Error message should contain '$errorMessage', but was '${state.message}'")
                        foundError = true
                    }
                    else -> {
                        fail("Unexpected state: $state")
                    }
                }
            }
            assertTrue(foundError, "Error state was not emitted")
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateFact should emit error state when any use case fails`() = runTest {
        // Arrange
        coEvery { getFactUseCase() } throws Exception("Failed to fetch fact")
        coEvery { getLatestFactUseCase() } throws Exception("Failed to get latest fact")
        coEvery { translateUseCase(any(), any(), any()) } throws Exception("Failed to translate fact")

        // Act
        viewModel.updateFact()

        // Assert
        viewModel.uiState.test {
            var foundError = false
            repeat(3) { // Allow for up to 3 emissions
                when (val state = awaitItem()) {
                    is TheResult.Loading -> {
                        println("Loading state received")
                    }
                    is TheResult.Error -> {
                        assertTrue(state.message.contains("Failed to fetch fact") || 
                                   state.message.contains("Failed to get latest fact") ||
                                   state.message.contains("Failed to translate fact"), 
                            "Error message should contain one of the expected error messages, but was '${state.message}'")
                        foundError = true
                    }
                    else -> {
                        fail("Unexpected state: $state")
                    }
                }
            }
            assertTrue(foundError, "Error state was not emitted")
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateFact should use correct language codes`() = runTest {
        // Arrange
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val translation = Translation("翻訳されたテスト事実")
        coEvery { getFactUseCase() } returns fact
        coEvery { translateUseCase("Test fact", "en", "ja") } returns translation
        coEvery { getLatestFactUseCase() } returns flowOf(fact.text)

        // Act
        viewModel.updateFact()

        // Assert
        viewModel.uiState.test {
            val loadingState = awaitItem()
            println("First emitted state: $loadingState")
            assertTrue(loadingState is TheResult.Loading)

            val successState = awaitItem()
            println("Second emitted state: $successState")
            assertTrue(successState is TheResult.Success)

            if (successState is TheResult.Success) {
                with(successState.data) {
                    assertEquals(fact.text, this.fact)
                    assertEquals(translation.translatedText, this.translationText)
                    assertEquals("en", this.sourceLanguage)
                    assertEquals("ja", this.targetLanguage)
                }
            } else {
                fail("Expected Success state, but got $successState")
            }

            expectNoEvents()
        }

        coVerify { getLatestFactUseCase() }
        coVerify { translateUseCase("Test fact", "en", "ja") }
    }

    @Test
    fun `getQuiz should update quizState with new quiz`() = runTest {
        val fact = "Test fact"
        val quiz = Quiz(listOf("Option 1", "Option 2"), "Question", "a")

        coEvery { getQuizUseCase(fact) } returns quiz

        viewModel.getQuiz(fact)

        viewModel.quizState.test {
            assertTrue(awaitItem() is TheResult.Loading)
            val result = awaitItem()
            assertTrue(result is TheResult.Success)
            assertEquals(quiz, (result as TheResult.Success).data)
        }
    }

    @Test
    fun `setLanguages should update languages and trigger translation`() = runTest {
        val sourceLang = "English"
        val targetLang = "Japanese"
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val translation = Translation("翻訳されたテストファクト")

        coEvery { translateUseCase(any(), any(), any()) } returns translation
        coEvery { getLatestFactUseCase() } returns flowOf(fact.text)

        viewModel.setLanguages(sourceLang, targetLang)

        viewModel.uiState.test {
            val result = awaitItem()
            assertTrue(result is TheResult.Success)
            assertEquals("ja", (result as TheResult.Success).data.targetLanguage)
            assertEquals(translation.translatedText, result.data.translationText)
        }
    }
}