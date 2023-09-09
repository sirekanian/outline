package org.sirekanyan.outline.ext

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.widget.Toast

private const val OUTLINE_PACKAGE = "org.outline.android.client"
private const val OUTLINE_PLAY_LINK = "https://play.google.com/store/apps/details?id=$OUTLINE_PACKAGE"

fun isOutlineInstalled(context: Context): Boolean =
    try {
        context.packageManager.getPackageInfo(OUTLINE_PACKAGE, GET_ACTIVITIES)
        true
    } catch (exception: NameNotFoundException) {
        false
    }

fun openOutline(context: Context) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(OUTLINE_PACKAGE)
        if (intent == null) {
            openGooglePlay(context)
        } else {
            context.startActivity(intent)
        }
    } catch (exception: Exception) {
        logDebug("Cannot open Outline", exception)
        openGooglePlay(context)
    }
}

fun openGooglePlay(context: Context) {
    try {
        context.startActivity(Intent(ACTION_VIEW, Uri.parse(OUTLINE_PLAY_LINK)))
    } catch (exception: Exception) {
        logDebug("Cannot open Google Play", exception)
        Toast.makeText(context, "Cannot open Google Play", Toast.LENGTH_SHORT).show()
    }
}
