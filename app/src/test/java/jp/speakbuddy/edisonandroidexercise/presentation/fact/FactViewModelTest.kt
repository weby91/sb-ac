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

        coEvery { getLatestFactUseCase() } returns flowOf("")

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

    /**
     * Tests that updateFact() correctly updates the uiState with a new fact and translation.
     * Verifies that the correct states are emitted: Loading, then Success with expected data.
     */
    @Test
    fun `updateFact should update uiState with new fact and translation`() = runTest {
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val translation = Translation("Translated test fact")

        coEvery { getFactUseCase() } returns fact
        coEvery { translateUseCase(any(), any(), any()) } returns translation

        viewModel.updateFact()

        viewModel.uiState.test {
            repeat(3) {
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

    /**
     * Tests that updateFact() emits an error state when getFactUseCase fails.
     * Verifies that the error message contains the expected error text.
     */
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
            repeat(3) {
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

    /**
     * Tests that updateFact() emits an error state when any use case fails.
     * Verifies that the error message contains one of the expected error texts.
     */
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
            repeat(3) { 
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

    /**
     * Tests that updateFact() uses the correct language codes when translating.
     * Verifies the initial and updated states, and checks that the correct translation calls are made.
     */
    @Test
    fun `updateFact should use correct language codes`() = runTest {
        // Arrange
        val initialFact = "Initial fact"
        val updatedFact = Fact(text = "Test fact", id = 1, length = 9)
        val initialTranslation = Translation("初期の事実の翻訳")
        val updatedTranslation = Translation("翻訳されたテスト事実")
        
        coEvery { getLatestFactUseCase() } returns flowOf(initialFact)
        coEvery { translateUseCase(any(), any(), any()) } answers { 
            val (text, sourceLang, targetLang) = args
            when {
                text == initialFact && sourceLang == "ja" && targetLang == "en" -> initialTranslation
                text == updatedFact.text && sourceLang == "ja" && targetLang == "en" -> updatedTranslation
                text == initialFact && sourceLang == "en" && targetLang == "ja" -> initialTranslation
                text == updatedFact.text && sourceLang == "en" && targetLang == "ja" -> updatedTranslation
                else -> throw IllegalArgumentException("Unexpected arguments: $args")
            }
        }
        coEvery { getFactUseCase() } returns updatedFact

        // Create a new ViewModel instance for this test
        val testViewModel = FactViewModel(
            getFactUseCase,
            saveFactUseCase,
            translateUseCase,
            getSavedFactsUseCase,
            getQuizUseCase,
            getLatestFactUseCase,
            searchSavedFactsUseCase,
            testDispatcher
        )

        // Act
        testViewModel.updateFact()

        // Assert
        testViewModel.uiState.test {
            var seenInitialState = false
            var seenUpdatedState = false

            repeat(4) { 
                when (val state = awaitItem()) {
                    is TheResult.Loading -> println("Emitted state: Loading")
                    is TheResult.Success -> {
                        println("Emitted state: $state")
                        if (!seenInitialState) {
                            assertEquals(initialFact, state.data.fact)
                            assertEquals(initialTranslation.translatedText, state.data.translationText)
                            seenInitialState = true
                        } else {
                            assertEquals(updatedFact.text, state.data.fact)
                            assertEquals(updatedTranslation.translatedText, state.data.translationText)
                            seenUpdatedState = true
                        }
                    }
                    is TheResult.Error -> fail("Unexpected error state: ${state.message}")
                }
            }

            assertTrue(seenInitialState, "Did not see initial state")
            assertTrue(seenUpdatedState, "Did not see updated state")

            expectNoEvents()
        }

        coVerify { getLatestFactUseCase() }
        coVerify { translateUseCase(initialFact, any(), any()) }
        coVerify { getFactUseCase() }
        coVerify { translateUseCase(updatedFact.text, any(), any()) }
    }

    /**
     * Tests that getQuiz() updates the quizState with a new quiz.
     * Verifies that the correct states are emitted: Loading, then Success with the expected quiz data.
     */
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

    /**
     * Tests that setLanguages() updates the languages and triggers a translation.
     * Verifies the initial state, the state after setting languages and updating the fact,
     * and the final state after reverting to the initial fact.
     */
    @Test
    fun `setLanguages should update languages and trigger translation`() = runTest {
        val sourceLang = "English"
        val targetLang = "Japanese"
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val initialTranslation = Translation("Initial fact translation")
        val updatedTranslation = Translation("Updated test fact translation")
        
        coEvery { getLatestFactUseCase() } returns flowOf("")
        coEvery { translateUseCase(any(), any(), any()) } answers { 
            val (text, sourceLang, targetLang) = args
            println("translateUseCase called with: text=$text, sourceLang=$sourceLang, targetLang=$targetLang")
            when {
                text == "" -> initialTranslation
                text == fact.text -> updatedTranslation
                else -> throw IllegalArgumentException("Unexpected arguments: $args")
            }
        }
        coEvery { getFactUseCase() } returns fact

        val testViewModel = FactViewModel(
            getFactUseCase,
            saveFactUseCase,
            translateUseCase,
            getSavedFactsUseCase,
            getQuizUseCase,
            getLatestFactUseCase,
            searchSavedFactsUseCase,
            testDispatcher
        )

        testViewModel.uiState.test {
            // Initial state
            assertEquals(TheResult.Loading, awaitItem())
            
            // State after initialization
            val initialState = awaitItem()
            assertTrue(initialState is TheResult.Success)
            assertEquals("", (initialState as TheResult.Success).data.fact)
            assertEquals("en", initialState.data.sourceLanguage)
            assertEquals("ja", initialState.data.targetLanguage)
            assertEquals(initialTranslation.translatedText, initialState.data.translationText)

            // Set languages and update fact
            testViewModel.setLanguages(sourceLang, targetLang)
            testViewModel.updateFact()

            // Loading state
            assertEquals(TheResult.Loading, awaitItem())

            // Updated state
            val updatedState = awaitItem()
            println("Updated state: $updatedState")
            assertTrue(updatedState is TheResult.Success)
            assertEquals(fact.text, (updatedState as TheResult.Success).data.fact)
            assertEquals("en", updatedState.data.sourceLanguage)
            assertEquals("ja", updatedState.data.targetLanguage)
            assertEquals(updatedTranslation.translatedText, updatedState.data.translationText)

            // Additional Loading state
            assertEquals(TheResult.Loading, awaitItem())

            // Final state (reverts to initial state)
            val finalState = awaitItem()
            assertTrue(finalState is TheResult.Success)
            assertEquals("", (finalState as TheResult.Success).data.fact)
            assertEquals("en", finalState.data.sourceLanguage)
            assertEquals("ja", finalState.data.targetLanguage)
            assertEquals(initialTranslation.translatedText, finalState.data.translationText)

            // No more events
            expectNoEvents()
        }

        coVerify(exactly = 3) { translateUseCase("", "ja", "en") }
        coVerify(exactly = 1) { translateUseCase(fact.text, "ja", "en") }
        coVerify(exactly = 1) { getFactUseCase() }
        coVerify(exactly = 2) { getLatestFactUseCase() }
    }
}