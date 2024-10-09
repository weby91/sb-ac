package jp.speakbuddy.edisonandroidexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.speakbuddy.edisonandroidexercise.commons.TheResult
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.model.Quiz
import jp.speakbuddy.edisonandroidexercise.use_case.GetFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetLatestFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetQuizUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SaveFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SearchSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.TranslateUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FactViewModel @Inject constructor(
    private val getFactUseCase: GetFactUseCase,
    private val saveFactUseCase: SaveFactUseCase,
    private val translateUseCase: TranslateUseCase,
    private val getSavedFactsUseCase: GetSavedFactsUseCase,
    private val getQuizUseCase: GetQuizUseCase,
    private val getLatestFactUseCase: GetLatestFactUseCase,
    private val searchSavedFactsUseCase: SearchSavedFactsUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    internal var showTranslation = true
    internal var sourceLanguage = "en"
    internal var targetLanguage = "ja"

    private val _uiState = MutableStateFlow<TheResult<FactUiState>>(TheResult.Loading)
    val uiState: StateFlow<TheResult<FactUiState>> = _uiState.asStateFlow()

    private val languageData = mapOf(
        "Japanese" to Triple("ja", "JP", "JP"),
        "Spanish" to Triple("es", "ES", "ES"),
        "French" to Triple("fr", "FR", "FR"),
        "German" to Triple("de", "DE", "DE"),
        "Italian" to Triple("it", "IT", "IT"),
        "Portuguese" to Triple("pt", "PT", "PT"),
        "Russian" to Triple("ru", "RU", "RU"),
        "Chinese" to Triple("zh", "CN", "CN"),
        "Korean" to Triple("ko", "KR", "KR"),
        "Arabic" to Triple("ar", "AR", "SA"),
        "Hindi" to Triple("hi", "IN", "IN"),
        "Dutch" to Triple("nl", "NL", "NL"),
        "Swedish" to Triple("sv", "SE", "SE"),
        "Polish" to Triple("pl", "PL", "PL"),
        "Czech" to Triple("cs", "CZ", "CZ"),
        "Romanian" to Triple("ro", "RO", "RO"),
        "Bulgarian" to Triple("bg", "BG", "BG"),
        "Hungarian" to Triple("hu", "HU", "HU"),
        "Thai" to Triple("th", "TH", "TH"),
        "Vietnamese" to Triple("vi", "VN", "VN"),
        "Indonesian" to Triple("id", "ID", "ID"),
        "Malay" to Triple("ms", "MS", "MY"),
        "Filipino" to Triple("fil", "PH", "PH")
    )

    private val _selectedLanguage = MutableStateFlow("")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            if (_uiState.value is TheResult.Loading) {
                getLatestFactOrFetchNew()
            }
        }
    }

    internal suspend fun getLatestFactOrFetchNew() {
        _uiState.value = TheResult.Loading
        try {
            val latestFact = getLatestFactUseCase()
            latestFact.collect { fact ->
                if (fact.isNotEmpty()) {
                    updateUiStateWithFact(
                        Fact(
                            text = fact,
                            id = 0,
                            length = fact.length
                        )
                    )
                } else {
                    // If latest fact is empty, fetch a new one
                    val newFact = getFactUseCase()
                    saveFactUseCase(newFact)
                    updateUiStateWithFact(newFact)
                }
            }
        } catch (e: Exception) {
            _uiState.value = TheResult.Error("Failed to fetch fact: ${e.message}")
        }
    }

    internal suspend fun updateUiStateWithFact(fact: Fact) {
        val translation = translateUseCase(
            text = fact.text,
            sourceLang = sourceLanguage, 
            targetLang = targetLanguage 
        )
        _uiState.value = TheResult.Success(
            FactUiState(
                fact = fact.text,
                translationText = translation.translatedText,
                showTranslation = showTranslation,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                length = fact.length,
                showLength = fact.length > 100,
                showMultipleCats = fact.text.contains("cats")
            )
        )
    }

    fun updateFact() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = TheResult.Loading
            try {
                val fact = getFactUseCase()
                saveFactUseCase(fact)
                updateUiStateWithFact(fact)
            } catch (e: Exception) {
                _uiState.value = TheResult.Error("Failed to fetch fact: ${e.message}")
            }
        }
    }

    internal fun updateFactWithTranslation() {
        val currentState = _uiState.value
        if (currentState is TheResult.Success) {
            if (currentState.data.fact.isNotEmpty()) {
                viewModelScope.launch {
                    _uiState.value = TheResult.Loading
                    try {
                        val translation = translateUseCase(
                            text = currentState.data.fact,
                            sourceLang = sourceLanguage,  // Changed order
                            targetLang = targetLanguage   // Changed order
                        )
                        _uiState.value = TheResult.Success(
                            currentState.data.copy(
                                translationText = translation.translatedText,
                                showTranslation = true,
                                sourceLanguage = sourceLanguage,
                                targetLanguage = targetLanguage
                            )
                        )
                    } catch (e: Exception) {
                        _uiState.value = TheResult.Error("Failed to translate: ${e.message}")
                    }
                }
            }
        }
    }

    fun setLanguages(source: String, target: String) {
        viewModelScope.launch {
            sourceLanguage = getLanguageCode(source)
            targetLanguage = getLanguageCode(target)
            updateFactWithTranslation()
        }
    }

    fun getFlagCodes(): Map<String, String> {
        return languageData.mapValues { it.value.second }
    }

    private fun getLanguageCode(language: String): String {
        return languageData[language]?.first ?: "en"
    }

    fun getSavedFacts(): Flow<PagingData<Fact>> {
        return getSavedFactsUseCase()
    }

    fun setSelectedLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun searchSavedFacts(query: String): Flow<PagingData<Fact>> {
        return searchSavedFactsUseCase(query)
    }

    private val _quizState = MutableStateFlow<TheResult<Quiz>>(TheResult.Loading)
    val quizState: StateFlow<TheResult<Quiz>> = _quizState.asStateFlow()

    fun getQuiz(fact: String) {
        viewModelScope.launch {
            _quizState.value = TheResult.Loading
            try {
                val quiz = getQuizUseCase(fact)
                _quizState.value = TheResult.Success(quiz)
            } catch (e: Exception) {
                _quizState.value = TheResult.Error("Failed to fetch quiz: ${e.message}")
            }
        }
    }
}

data class FactUiState(
    val fact: String = "",
    val showLength: Boolean = false,
    val length: Int = 0,
    val showMultipleCats: Boolean = false,
    val sourceLanguage: String = "en",
    val targetLanguage: String = "Japanese",
    val translationText: String = "",
    val showTranslation: Boolean = true
)

