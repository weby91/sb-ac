package jp.speakbuddy.edisonandroidexercise.data.repository

import jp.speakbuddy.edisonandroidexercise.data.dto.QuizRequestDto
import jp.speakbuddy.edisonandroidexercise.data.remote.QuizApi
import jp.speakbuddy.edisonandroidexercise.domain.model.Quiz
import jp.speakbuddy.edisonandroidexercise.domain.repository.QuizRepository
import javax.inject.Inject

/**
 * Implementation of the QuizRepository interface that interacts with the QuizApi.
 *
 * @property api The API client for interacting with quiz data.
 */
class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApi,
) : QuizRepository {
    /**
     * Fetches a quiz based on the provided content.
     *
     * @param content The content to generate a quiz for.
     * @return A Quiz object containing the question and options.
     */
    override suspend fun getQuiz(content: String): Quiz {
        val response = api.getQuiz(QuizRequestDto(content))
        return Quiz(options = response.options, question = response.question, answer = response.answer)
    }
}