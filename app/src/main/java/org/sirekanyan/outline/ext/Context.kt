package org.sirekanyan.outline.ext

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openGooglePlay(uri: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    } catch (exception: Exception) {
        logDebug("Cannot open Google Play", exception)
        showToast("Cannot open Google Play")
    }
}
