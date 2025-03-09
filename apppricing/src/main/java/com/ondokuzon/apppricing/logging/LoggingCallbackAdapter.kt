package com.ondokuzon.apppricing.logging


internal class LoggingCallbackAdapter(
    private val isLoggingEnabled: Boolean,
    private val payBoxLoggingCallback: LoggingCallback?
) : LoggingCallback {

    private val loggingCallbackList = arrayListOf<LoggingCallback>().apply {
        add(DebugLoggingCallback(isLoggingEnabled))
        payBoxLoggingCallback?.let { add(it) }
    }

    override fun log(loggingMessage: LoggingMessage) {
        loggingCallbackList.forEach { it.log(loggingMessage.cloneWithSuffix(createVersionSuffixMessage())) }
    }

    private fun createVersionSuffixMessage(): String {
      return " [ appPricingVersion - 0.0.1]"// TODO + Buidconfig.VERSION
    }
}