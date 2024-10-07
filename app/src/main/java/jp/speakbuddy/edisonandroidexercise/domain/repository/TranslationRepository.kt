package jp.speakbuddy.edisonandroidexercise.domain.repository

import jp.speakbuddy.edisonandroidexercise.domain.model.Translation

/**
 * TranslationRepository is an interface that defines the contract for data operations
 * related to translations. It provides methods to retrieve a single translation, get the latest
 * translation as a flow of strings, and save a new translation.
 *
 * Implementations of this interface should handle the data source logic,
 * whether it be from a local database, a remote API, or any other data source.
 */
interface TranslationRepository {
    suspend fun translate(text: String, targetLang: String, sourceLang: String): Translation
}