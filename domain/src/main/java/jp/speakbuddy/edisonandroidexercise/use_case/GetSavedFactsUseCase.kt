package jp.speakbuddy.edisonandroidexercise.use_case

import androidx.paging.PagingData
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.repository.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedFactsUseCase @Inject constructor(
    private val repository: FactRepository
) {
    operator fun invoke(): Flow<PagingData<Fact>> {
        return repository.getSavedFacts()
    }
}