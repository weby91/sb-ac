package jp.speakbuddy.edisonandroidexercise.use_case

import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.repository.repository.FactRepository
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
class SaveFactUseCase @Inject constructor(
    private val repository: FactRepository
) {
    suspend operator fun invoke(fact: Fact) {
        repository.saveFact(fact)
    }
}