package jp.speakbuddy.edisonandroidexercise.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FactDao {
    @Query("SELECT * FROM facts ORDER BY id DESC LIMIT 1")
    fun getLatestFact(): Flow<FactEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: FactEntity)

    @Query("SELECT * FROM facts ORDER BY id DESC")
    fun getSavedFacts(): PagingSource<Int, FactEntity>

    @Query("SELECT * FROM facts WHERE fact LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchSavedFacts(query: String): PagingSource<Int, FactEntity>
}