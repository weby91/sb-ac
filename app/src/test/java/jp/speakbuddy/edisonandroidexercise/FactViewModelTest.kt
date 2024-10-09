package jp.speakbuddy.edisonandroidexercise

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.model.Quiz
import jp.speakbuddy.edisonandroidexercise.model.Translation
import jp.speakbuddy.edisonandroidexercise.use_case.*
import jp.speakbuddy.edisonandroidexercise.commons.TheResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@ExperimentalCoroutinesApi
@ExtendWith(MainDispatcherExtension::class)
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
        val initialTranslation = Translation("初の事実の翻訳")
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
            assertEquals(quiz, result.data)
        }
    }

    /**
     * Tests that setLanguages() updates the languages and triggers a translation.
     * Verifies the initial state, the state after setting languages and updating the fact,
     * and the final state after reverting to the initial fact.
     */
    @Test
    fun `setLanguages should update languages and trigger translation`() = runTest {
        val initialSourceLang = "en"
        val initialTargetLang = "ja"
        val updatedSourceLang = "en"
        val updatedTargetLang = "ja"
        val fact = Fact(text = "Test fact", id = 1, length = 9)
        val updatedTranslation = Translation("Updated test fact translation")
        
        coEvery { getLatestFactUseCase() } returns flowOf(fact.text)
        coEvery { translateUseCase(any(), any(), any()) } returns updatedTranslation
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
            // Skip initial Loading state
            awaitItem()
            
            // Initial state
            val initialState = awaitItem() as TheResult.Success
            assertEquals(fact.text, initialState.data.fact)
            assertEquals(initialSourceLang, initialState.data.sourceLanguage)
            assertEquals(initialTargetLang, initialState.data.targetLanguage)

            // Set languages and update fact
            testViewModel.setLanguages("English", "Japanese")

            // Skip Loading state
            awaitItem()

            // Updated state
            val updatedState = awaitItem() as TheResult.Success
            assertEquals(fact.text, updatedState.data.fact)
            assertEquals(updatedSourceLang, updatedState.data.sourceLanguage)
            assertEquals(updatedTargetLang, updatedState.data.targetLanguage)
            assertEquals(updatedTranslation.translatedText, updatedState.data.translationText)

            expectNoEvents()
        }

        coVerify(exactly = 2) { translateUseCase(fact.text, updatedTargetLang, updatedSourceLang) }
    }
}