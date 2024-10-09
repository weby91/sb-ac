package jp.speakbuddy.edisonandroidexercise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Database class for storing facts.
 */
@Database(entities = [FactEntity::class], version = 1)
abstract class FactDatabase : RoomDatabase() {
    abstract fun factDao(): FactDao
}