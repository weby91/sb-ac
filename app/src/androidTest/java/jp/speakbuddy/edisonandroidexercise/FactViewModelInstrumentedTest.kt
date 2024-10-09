package jp.speakbuddy.edisonandroidexercise

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jp.speakbuddy.edisonandroidexercise.commons.TheResult
import jp.speakbuddy.edisonandroidexercise.use_case.GetFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetLatestFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetQuizUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SaveFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SearchSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.TranslateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class)
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
    fun testGetQuiz() = runTest {
        viewModel.getQuiz("Cat is an animal that has 4 legs. It has tail, two eyes, one nose. It can jump very high.")

        // Wait for the operation to complete
        Thread.sleep(3000)

        val quizState = viewModel.quizState.value
        assertTrue(quizState is TheResult.Success)
    }

    @Test
    fun testAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("jp.speakbuddy.edisonandroidexercise", appContext.packageName)
    }
}