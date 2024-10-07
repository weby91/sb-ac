package jp.speakbuddy.edisonandroidexercise.data

import FactPagingSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.PagingSource
import jp.speakbuddy.edisonandroidexercise.data.local.FactDao
import jp.speakbuddy.edisonandroidexercise.data.local.FactEntity
import jp.speakbuddy.edisonandroidexercise.domain.model.Fact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * FactStorage is responsible for managing the storage and retrieval of facts.
 * It interacts with the local database through the FactDao to provide
 * the latest fact and to save new facts.
 *
 * @property factDao The Data Access Object for accessing fact data in the local database.
 */

/**
 * Retrieves the latest fact as a Flow of String.
 * It maps the FactEntity from the database to a String representation of the fact.
 *
 * @return A Flow that emits the latest fact as a String.
 */

/**
 * Saves the latest fact to the local database.
 *
 * @param fact The fact to be saved as a String.
 */
class FactStorage @Inject constructor(
    private val factDao: FactDao
) {

    val latestFact: Flow<String> = factDao.getLatestFact().map { factEntity ->
        factEntity?.fact ?: ""
    }

    suspend fun saveLatestFact(fact: String) {
        factDao.insertFact(FactEntity(fact = fact))
    }

    fun getSavedFacts(): PagingSource<Int, Fact> {
        val originalSource: PagingSource<Int, FactEntity> = factDao.getSavedFacts()
        val factPagingSource: PagingSource<Int, Fact> = FactPagingSource(originalSource)
        return factPagingSource
    }
}