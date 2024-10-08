package jp.speakbuddy.edisonandroidexercise.domain.use_case

import androidx.paging.PagingData
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import jp.speakbuddy.edisonandroidexercise.domain.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchSavedFactsUseCase @Inject constructor(
    private val repository: FactRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Fact>> {
        return repository.searchSavedFacts(query)
    }
}