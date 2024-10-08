package jp.speakbuddy.edisonandroidexercise.use_case

import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.repository.repository.FactRepository
import javax.inject.Inject

/**
 * Use case for retrieving a random fact.
 *
 * This class is responsible for executing the business logic of fetching a random fact
 * from the repository. It acts as a single point of entry for this specific use case,
 * abstracting the data source from the presentation layer.
 *
 * @property repository The repository that provides access to the fact data source.
 */
class GetFactUseCase @Inject constructor(
    private val repository: FactRepository
) {
    suspend operator fun invoke(): Fact {
        return repository.getFact()
    }
}