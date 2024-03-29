package org.sirekanyan.outline.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import org.sirekanyan.outline.R
import java.net.ConnectException
import java.net.UnknownHostException

@Composable
fun rememberStateScope(): CoroutineScope {
    val context = LocalContext.current
    val scope = rememberCoroutineScope {
        CoroutineExceptionHandler { _, throwable ->
            if (throwable is UnknownHostException) {
                logError("Uncaught exception: ${throwable.message}", throwable = null)
            } else {
                logError("Uncaught exception", throwable)
            }
            when (throwable) {
                is UnknownHostException, is ConnectException -> {
                    context.showToast(R.string.outln_toast_check_network)
                }
                else -> {
                    context.showToast(R.string.outln_toast_something_wrong)
                }
            }
        }
    }
    return remember { scope + SupervisorJob(scope.coroutineContext.job) }
}
