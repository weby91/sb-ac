package jp.speakbuddy.edisonandroidexercise.presentation.fact

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetLatestFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetQuizUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.GetSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.SaveFactUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.SearchSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.domain.use_case.TranslateUseCase
import jp.speakbuddy.edisonandroidexercise.presentation.commons.TheResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class FactViewModelInstrumentedTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var getFactUseCase: GetFactUseCase

    @Inject
    lateinit var saveFactUseCase: SaveFactUseCase

    @Inject
    lateinit var translateUseCase: TranslateUseCase

    @Inject
    lateinit var getSavedFactsUseCase: GetSavedFactsUseCase

    @Inject
    lateinit var getQuizUseCase: GetQuizUseCase

    @Inject
    lateinit var getLatestFactUseCase: GetLatestFactUseCase

    @Inject
    lateinit var searchSavedFactsUseCase: SearchSavedFactsUseCase

    private lateinit var viewModel: FactViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        viewModel = FactViewModel(
            getFactUseCase,
            saveFactUseCase,
            translateUseCase,
            getSavedFactsUseCase,
            getQuizUseCase,
            getLatestFactUseCase,
            searchSavedFactsUseCase
        )
    }

    @Test
    fun testInitialState() = runTest {
        val initialState = viewModel.uiState.value
        assertTrue(initialState is TheResult.Loading)
    }

    @Test
    fun testUpdateFact() = runTest {
        viewModel.updateFact()

        // Wait for the operation to complete
        Thread.sleep(2000)

        val updatedState = viewModel.uiState.value
        assertTrue(updatedState is TheResult.Success)

        if (updatedState is TheResult.Success) {
            assertTrue(updatedState.data.fact.isNotEmpty())
            assertTrue(updatedState.data.translationText.isNotEmpty())
        }
    }

    @Test
    fun testSetLanguages() = runTest {
        viewModel.setLanguages("English", "Japanese")

        // Wait for the operation to complete
        Thread.sleep(2000)

        val updatedState = viewModel.uiState.value
        assertTrue(updatedState is TheResult.Success)

        if (updatedState is TheResult.Success) {
            assertEquals("en", updatedState.data.sourceLanguage)
            assertEquals("ja", updatedState.data.targetLanguage)
        }
    }

    @Test
    fun testGetQuiz() = runTest {
        viewModel.getQuiz("Test fact")

        // Wait for the operation to complete
        Thread.sleep(2000)

        val quizState = viewModel.quizState.value
        assertTrue(quizState is TheResult.Success)
    }

    @Test
    fun testAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("jp.speakbuddy.edisonandroidexercise", appContext.packageName)
    }
}