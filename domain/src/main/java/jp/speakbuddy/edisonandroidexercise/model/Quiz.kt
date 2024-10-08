package jp.speakbuddy.edisonandroidexercise.model

data class Quiz(
    val options: List<String>,
    val question: String,
    val answer: String
)