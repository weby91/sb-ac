package jp.speakbuddy.edisonandroidexercise.domain.use_case

import jp.speakbuddy.edisonandroidexercise.domain.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the latest fact.
 *
 * This class is responsible for executing the business logic of fetching the latest fact
 * from the repository. It acts as a single point of entry for this specific use case,
 * abstracting the data source from the presentation layer.
 *
 * @property repository The repository that provides access to the fact data source.
 */
class GetLatestFactUseCase @Inject constructor(
    private val repository: FactRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.getLatestFact()
    }
}