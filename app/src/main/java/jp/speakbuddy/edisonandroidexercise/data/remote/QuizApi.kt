package jp.speakbuddy.edisonandroidexercise.data.remote

import jp.speakbuddy.edisonandroidexercise.data.dto.QuizDto
import jp.speakbuddy.edisonandroidexercise.data.dto.QuizRequestDto
import jp.speakbuddy.edisonandroidexercise.data.dto.SubmitAnswerRequestDto
import jp.speakbuddy.edisonandroidexercise.data.dto.SubmitAnswerResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for interacting with the Quiz API.
 */ 
interface QuizApi {
    /**
     * Retrieves a random quiz from the API.
     *
     * @return a [QuizDto] object containing the quiz
     */
    @POST("get-quiz")
    suspend fun getQuiz(@Body body: QuizRequestDto): QuizDto

    /**
     * Submits the answer to the quiz.
     *
     * @return a [SubmitAnswerResponseDto] object containing the response
     */
    @POST("submit-answer")
    suspend fun submitAnswer(@Body body: SubmitAnswerRequestDto): SubmitAnswerResponseDto
}