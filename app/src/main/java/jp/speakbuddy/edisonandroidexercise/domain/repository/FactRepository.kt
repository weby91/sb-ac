package jp.speakbuddy.edisonandroidexercise.domain.repository

import androidx.paging.PagingData
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import kotlinx.coroutines.flow.Flow

/**
 * FactRepository is an interface that defines the contract for data operations
 * related to facts. It provides methods to retrieve a single fact, get the latest
 * fact as a flow of strings, and save a new fact.
 *
 * Implementations of this interface should handle the data source logic,
 * whether it be from a local database, a remote API, or any other data source.
 */
    interface FactRepository {
    /**
     * Retrieves a single fact from the data source.
     *
     * @return a single [Fact] object
     */
    suspend fun getFact(): Fact

    /**
     * Retrieves the latest fact as a flow of strings.  
     *
     * @return a flow of strings representing the latest fact
     */
    fun getLatestFact(): Flow<String>

    /**
     * Saves a new fact to the data source.
     *
     * @param fact the [Fact] object to be saved
     */
    suspend fun saveFact(fact: Fact)

    /**
     * Retrieves saved facts as a paging source.
     *
     * @return a paging source of facts
     */
    fun getSavedFacts(): Flow<PagingData<Fact>>
}