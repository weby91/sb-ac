package jp.speakbuddy.edisonandroidexercise.repository.repository

import jp.speakbuddy.edisonandroidexercise.model.Quiz

interface QuizRepository {
    suspend fun getQuiz(content: String): Quiz
}