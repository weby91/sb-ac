package jp.speakbuddy.edisonandroidexercise.data.repository

import jp.speakbuddy.edisonandroidexercise.data.dto.QuizRequestDto
import jp.speakbuddy.edisonandroidexercise.data.dto.SubmitAnswerRequestDto
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

    /**
     * Submits an answer to a quiz question and retrieves the result.
     *
     * @param fact The fact related to the quiz.
     * @param question The quiz question being answered.
     * @param options The list of options for the quiz question.
     * @param userAnswer The user's selected answer.
     * @return A string indicating the result of the answer submission.
     */
    override suspend fun submitAnswer(
        fact: String,
        question: String,
        options: List<String>,
        userAnswer: String
    ): String {
        val request = SubmitAnswerRequestDto(fact, question, options, userAnswer)
        val response = api.submitAnswer(request)
        return response.result
    }
}