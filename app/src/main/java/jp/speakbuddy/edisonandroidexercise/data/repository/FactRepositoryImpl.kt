package jp.speakbuddy.edisonandroidexercise.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.speakbuddy.edisonandroidexercise.data.FactStorage
import jp.speakbuddy.edisonandroidexercise.data.remote.FactApi
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import jp.speakbuddy.edisonandroidexercise.domain.repository.FactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the FactRepository interface that interacts with the FactApi and FactStorage.
 *
 * @property api The API client for interacting with the fact data.
 * @property factStorage The storage for managing the latest fact.
 */
class FactRepositoryImpl @Inject constructor(
    private val api: FactApi,
    private val factStorage: FactStorage
) : FactRepository {
    override suspend fun getFact(): Fact {
        val response = api.getFact()
        return Fact(0, response.fact, response.length)
    }

    override fun getLatestFact(): Flow<String> {
        return factStorage.latestFact
    }

    override suspend fun saveFact(fact: Fact) {
        factStorage.saveLatestFact(fact.text)
    }

    override fun getSavedFacts(): Flow<PagingData<Fact>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { factStorage.getSavedFacts() }
        ).flow
            .map { pagingData ->
                pagingData.map { factEntity ->
                    Fact(factEntity.id, factEntity.text, factEntity.length)
                }
            }
    }

    override fun searchSavedFacts(query: String): Flow<PagingData<Fact>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { factStorage.searchSavedFacts(query) }
        ).flow
            .map { pagingData ->
                pagingData.map { factEntity ->
                    Fact(factEntity.id, factEntity.text, factEntity.length)
                }
            }
    }
}