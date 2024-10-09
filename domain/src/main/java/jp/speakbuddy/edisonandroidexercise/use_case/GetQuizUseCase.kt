package jp.speakbuddy.edisonandroidexercise.use_case

import jp.speakbuddy.edisonandroidexercise.model.Quiz
import jp.speakbuddy.edisonandroidexercise.repository.repository.QuizRepository
import javax.inject.Inject

class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(content: String): Quiz {
        return repository.getQuiz(content)
    }
}