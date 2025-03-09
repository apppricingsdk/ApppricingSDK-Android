package com.ondokuzon.apppricing.logging

import android.util.Log

private const val LOG_TAG = "AppPricingLogger"

class DebugLoggingCallback(private val isLoggingEnabled: Boolean) : LoggingCallback {

    override fun log(loggingMessage: LoggingMessage) {
        if (!isLoggingEnabled) return

        when (loggingMessage) {
            is LoggingMessage.ErrorMessage -> Log.v(
                LOG_TAG,
                "AppPricing Error: ${loggingMessage.message} : ${loggingMessage.error.message.toString()}"
            )

            is LoggingMessage.InfoMessage -> Log.v(
                LOG_TAG,
                "AppPricing Info: ${loggingMessage.message}"
            )
        }
    }
}