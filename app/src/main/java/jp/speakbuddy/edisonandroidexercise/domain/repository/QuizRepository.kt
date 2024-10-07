package jp.speakbuddy.edisonandroidexercise.domain.repository

import jp.speakbuddy.edisonandroidexercise.domain.model.Quiz

interface QuizRepository {
    suspend fun getQuiz(content: String): Quiz
    suspend fun submitAnswer(fact: String, question: String, options: List<String>, userAnswer: String): String
}