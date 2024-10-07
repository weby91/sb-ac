package jp.speakbuddy.edisonandroidexercise.domain.use_case

import jp.speakbuddy.edisonandroidexercise.domain.model.Translation
import jp.speakbuddy.edisonandroidexercise.domain.repository.TranslationRepository
import javax.inject.Inject

/**
 * Use case for saving a fact.
 *
 * This class is responsible for executing the business logic of saving a fact
 * to the repository. It acts as a single point of entry for this specific use case,
 * abstracting the data source from the presentation layer.
 *
 * @property repository The repository that provides access to the fact data source.
 */
class TranslateUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    suspend operator fun invoke(text: String, targetLang: String, sourceLang: String): Translation {
        return repository.translate(text, targetLang, sourceLang)
    }
}