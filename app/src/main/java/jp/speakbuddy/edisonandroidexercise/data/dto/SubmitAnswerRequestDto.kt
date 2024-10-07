package jp.speakbuddy.edisonandroidexercise.data.dto

data class SubmitAnswerRequestDto(
    val fact: String,
    val question: String,
    val options: List<String>,
    val answer: String
)