package jp.speakbuddy.edisonandroidexercise.data.dto

data class QuizDto(
    val options: List<String>,
    val question: String,
    val answer: String
)

data class QuizRequestDto(
    val content: String
)