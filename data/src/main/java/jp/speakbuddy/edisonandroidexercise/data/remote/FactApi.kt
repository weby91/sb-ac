package jp.speakbuddy.edisonandroidexercise.data.remote

import jp.speakbuddy.edisonandroidexercise.data.dto.FactDto
import retrofit2.http.GET

/**
 * Interface for interacting with the Cat Facts API.
 */ 
interface FactApi {
    /**
     * Retrieves a random cat fact from the API.
     *
     * @return a [FactDto] object containing the fact and its length
     */
    @GET("fact")
    suspend fun getFact(): FactDto
}