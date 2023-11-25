package org.sirekanyan.outline.ext

import android.util.Log
import org.sirekanyan.outline.CrashReporter
import java.util.concurrent.CancellationException

private const val TAG = "Outline"

fun logDebug(message: String, throwable: Throwable?) {
    sendCrashReport(message, throwable)
    Log.d(TAG, message, throwable)
}

fun logWarn(message: String, throwable: Throwable?) {
    sendCrashReport(message, throwable)
    Log.w(TAG, message, throwable)
}

fun logError(message: String, throwable: Throwable?) {
    sendCrashReport(message, throwable)
    Log.e(TAG, message, throwable)
}

private fun sendCrashReport(message: String, throwable: Throwable?) {
    if (throwable is CancellationException) {
        Log.d(TAG, "Skipped sending crash report")
    } else {
        CrashReporter.handleException(throwable ?: Exception(message))
    }
}
