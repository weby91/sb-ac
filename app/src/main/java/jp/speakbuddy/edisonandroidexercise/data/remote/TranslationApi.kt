package jp.speakbuddy.edisonandroidexercise.data.remote

import jp.speakbuddy.edisonandroidexercise.data.dto.TranslationDto
import jp.speakbuddy.edisonandroidexercise.data.dto.TranslationRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for interacting with the Translation API.
 */ 
interface TranslationApi {
    /**
     * Retrieves a translation from the API.
     *
     * @return a [TranslationDto] object containing the translation
     */
    @POST("translate")
    suspend fun translate(@Body translationRequestDto: TranslationRequestDto): TranslationDto
}