package jp.speakbuddy.edisonandroidexercise.data.dto

data class TranslationDto(
    val translated_text: String
)

data class TranslationRequestDto(
    val text: String,
    val target_lang: String,
    val source_lang: String
)