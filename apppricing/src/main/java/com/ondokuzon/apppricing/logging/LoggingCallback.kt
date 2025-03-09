package com.ondokuzon.apppricing.logging

fun interface LoggingCallback {
    fun log(loggingMessage: LoggingMessage)
}