package jp.speakbuddy.edisonandroidexercise.domain.use_case

import jp.speakbuddy.edisonandroidexercise.domain.repository.QuizRepository
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(fact: String, question: String, options: List<String>, userAnswer: String): String {
        return repository.submitAnswer(fact, question, options, userAnswer)
    }
}