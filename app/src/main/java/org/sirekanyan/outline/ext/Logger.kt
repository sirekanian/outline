package org.sirekanyan.outline.ext

import android.util.Log
import org.sirekanyan.outline.CrashReporter

private const val TAG = "Outline"

fun logDebug(message: String, throwable: Throwable?) {
    CrashReporter.handleException(throwable ?: Exception(message))
    Log.d(TAG, message, throwable)
}

fun logWarn(message: String, throwable: Throwable?) {
    CrashReporter.handleException(throwable ?: Exception(message))
    Log.w(TAG, message, throwable)
}

fun logError(message: String, throwable: Throwable?) {
    CrashReporter.handleException(throwable ?: Exception(message))
    Log.e(TAG, message, throwable)
}
