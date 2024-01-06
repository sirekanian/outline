package org.sirekanyan.outline.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.sirekanyan.outline.R

fun Context.openGooglePlay(uri: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    } catch (exception: Exception) {
        logDebug("Cannot open Google Play", exception)
        showToast(R.string.outln_toast_cannot_open_play)
    }
}
