package com.ondokuzon.apppricing.error
fun interface ErrorCallback {
    fun onError(error: Throwable)
}