package jp.speakbuddy.edisonandroidexercise.domain.use_case

import jp.speakbuddy.edisonandroidexercise.domain.model.Quiz
import jp.speakbuddy.edisonandroidexercise.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(content: String): Quiz {
        return repository.getQuiz(content)
    }
}