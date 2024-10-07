package jp.speakbuddy.edisonandroidexercise.domain.use_case

import androidx.paging.PagingData
import androidx.paging.PagingSource
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import jp.speakbuddy.edisonandroidexercise.domain.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedFactsUseCase @Inject constructor(
    private val repository: FactRepository
) {
    operator fun invoke(): Flow<PagingData<Fact>> {
        return repository.getSavedFacts()
    }
}