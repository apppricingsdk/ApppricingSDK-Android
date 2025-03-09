package com.ondokuzon.apppricing.logging

sealed class LoggingMessage(open val message: String?) {

    data class ErrorMessage(override val message: String? = null, val error: Throwable) :
        LoggingMessage(message)

    data class InfoMessage(override val message: String) : LoggingMessage(message)
}

fun LoggingMessage.cloneWithSuffix(suffix: String?): LoggingMessage {
    return suffix?.let {
        when (this) {
            is LoggingMessage.ErrorMessage ->
                LoggingMessage.ErrorMessage(this.message + suffix, this.error)

            is LoggingMessage.InfoMessage ->
                LoggingMessage.InfoMessage(this.message + suffix)
        }
    } ?: this
}