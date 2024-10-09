package jp.speakbuddy.edisonandroidexercise.data.repository

import jp.speakbuddy.edisonandroidexercise.data.dto.TranslationRequestDto
import jp.speakbuddy.edisonandroidexercise.data.remote.TranslationApi
import jp.speakbuddy.edisonandroidexercise.model.Translation
import jp.speakbuddy.edisonandroidexercise.repository.repository.TranslationRepository
import javax.inject.Inject

/**
 * Implementation of the TranslationRepository interface that interacts with the TranslationApi.
 *
 * @property api The API client for interacting with the translation data.
 */
class TranslationRepositoryImpl @Inject constructor(
    private val api: TranslationApi,
) : TranslationRepository {
    override suspend fun translate(text: String, targetLang: String, sourceLang: String): Translation {
        val translationRequestDto = TranslationRequestDto(text, targetLang, sourceLang)
        val response = api.translate(translationRequestDto)
        return Translation(response.translated_text)
    }
}