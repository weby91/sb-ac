package jp.speakbuddy.edisonandroidexercise.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a fact in the local database.
 */
@Entity(tableName = "facts")
data class FactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fact: String,
    val timestamp: Long = System.currentTimeMillis()
)