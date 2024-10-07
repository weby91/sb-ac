package jp.speakbuddy.edisonandroidexercise.presentation.commons

/**
 * TheResult is a sealed class that represents the result of an operation.
 * It can be in one of three states: Loading, Success, or Error.
 * 
 * - Loading: Indicates that the operation is still in progress.
 * - Success: Indicates that the operation was successful, with a generic type T.
 * - Error: Indicates that the operation encountered an error, with a message.
 */
sealed class TheResult<out T> {
    data object Loading : TheResult<Nothing>()
    data class Success<out T>(val data: T) : TheResult<T>()
    data class Error(val message: String) : TheResult<Nothing>()
}